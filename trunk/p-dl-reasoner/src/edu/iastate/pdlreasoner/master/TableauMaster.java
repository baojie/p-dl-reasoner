package edu.iastate.pdlreasoner.master;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelClosedException;
import org.jgroups.ChannelException;
import org.jgroups.ChannelFactory;
import org.jgroups.ChannelNotConnectedException;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import edu.iastate.pdlreasoner.exception.IllegalQueryException;
import edu.iastate.pdlreasoner.kb.ImportGraph;
import edu.iastate.pdlreasoner.kb.Query;
import edu.iastate.pdlreasoner.kb.QueryResult;
import edu.iastate.pdlreasoner.message.BackwardConceptReport;
import edu.iastate.pdlreasoner.message.BranchTokenMessage;
import edu.iastate.pdlreasoner.message.Clash;
import edu.iastate.pdlreasoner.message.Exit;
import edu.iastate.pdlreasoner.message.ForwardConceptReport;
import edu.iastate.pdlreasoner.message.MakeGlobalRoot;
import edu.iastate.pdlreasoner.message.MessageToMaster;
import edu.iastate.pdlreasoner.message.Null;
import edu.iastate.pdlreasoner.message.ResumeExpansion;
import edu.iastate.pdlreasoner.message.SyncPing;
import edu.iastate.pdlreasoner.message.TableauMasterMessageProcessor;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.net.ChannelUtil;
import edu.iastate.pdlreasoner.struct.BiMap;
import edu.iastate.pdlreasoner.struct.MultiValuedMap;
import edu.iastate.pdlreasoner.struct.Ring;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;
import edu.iastate.pdlreasoner.util.CollectionUtil;
import edu.iastate.pdlreasoner.util.Profiler;
import edu.iastate.pdlreasoner.util.Timers;

public class TableauMaster {

	private static final Logger LOGGER = Logger.getLogger(TableauMaster.class);
	private static final long SLEEP_TIME = 500;
	
	private static enum State { ENTRY, EXPAND, CLASH, EXIT }
	
	private ChannelFactory m_ChannelFactory;
	private BlockingQueue<Message> m_MessageQueue;
	private State m_State;
	private Channel m_Channel;
	private Address m_Self;
	
	private BiMap<Address, PackageID> m_Slaves;
	private Ring<PackageID> m_Tableaux;
	private ImportGraph m_ImportGraph;
	private InterTableauManager m_InterTableauMan;
	private SyncManager m_SyncMan;
	private Set<BranchPointSet> m_ClashCauses;
	private QueryResult m_Result;
	
	//Processors
	private TableauMasterMessageProcessor m_MessageProcessor;
	
	public TableauMaster(ChannelFactory channelFactory) {
		m_ChannelFactory = channelFactory;
		m_MessageQueue = new LinkedBlockingQueue<Message>();
		m_State = State.ENTRY;
		m_Slaves = new BiMap<Address, PackageID>();
	}
	
	public QueryResult run(Query query, int numSlaves) throws ChannelException, IllegalQueryException {		
		Timers.start("network");
		initChannel();
		waitForSlavesToConnect(numSlaves);
		Timers.stop("network");
		Timers.start("reason");
		
		while (m_State != State.EXIT) {
			switch (m_State) {
			case ENTRY:
				getSlaveData(numSlaves);
				
				if (!query.isUnderstandableByWitness(m_ImportGraph)) {
					throw new IllegalQueryException("Query is not understandable by the specified witness package.");
				}
				
				broadcast(m_ImportGraph);
				initMaster(query);
				startExpansion(query);
				
				break;
				
			case EXPAND:
				m_SyncMan.restartSync();
				while (m_State == State.EXPAND && (!m_MessageQueue.isEmpty() || !m_SyncMan.isSynchronized())) {
					processOneTableauMessage();
				}
				
				if (m_State != State.EXPAND) break;
				
				exitWithResult(true);
				break;
				
			case CLASH:
				m_SyncMan.restartSync();
				while (m_State == State.CLASH && (!m_MessageQueue.isEmpty() || !m_SyncMan.isSynchronized())) {
					processOneTableauMessage();
				}
				
				if (m_State != State.CLASH) break;
				
				resume();
				break;
			}
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Disconnecting and closing channel");
		}
		
		Timers.stop("reason");
		
		Timers.start("network");
		waitForSlavesToDisconnect();
		m_Channel.disconnect();
		m_Channel.close();	
		Timers.stop("network");
		
		return m_Result;
	}
	
	public void send(PackageID destID, Serializable msg) {
		Address dest = m_Slaves.getA(destID);
		Message channelMsg = new Message(dest, m_Self, msg);
		try {
			m_Channel.send(channelMsg);
			
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Sent " + msg);
			}
		} catch (ChannelNotConnectedException e) {
			throw new RuntimeException(e);
		} catch (ChannelClosedException e) {
			throw new RuntimeException(e);
		}
		
		m_SyncMan.intercept(destID, msg);
		
		Profiler.INSTANCE.countMessage();
	}

	private Message takeOneMessage() {
		Message msg = null;
		do {
			try {
				msg = m_MessageQueue.take();
			} catch (InterruptedException e) {}
		} while (msg == null);
		
		Profiler.INSTANCE.countMessage();
		
		return msg;
	}

	private void processOneTableauMessage() {
		Message msg = takeOneMessage();
		MessageToMaster tabMsg = (MessageToMaster) msg.getObject();
		
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Received " + tabMsg);
		}
		
		tabMsg.execute(m_MessageProcessor);
	}
	
	private void broadcast(Serializable msg) {
		for (PackageID packageID : m_Tableaux) {
			send(packageID, msg);
		}
	}

	private void initChannel() throws ChannelException {
		m_Channel = m_ChannelFactory.createChannel();
		m_Channel.connect(ChannelUtil.getSessionName());
		m_Channel.setReceiver(new ReceiverAdapter() {
				public void receive(Message msg) {
					while (true) {
						try {
							m_MessageQueue.put(msg);
							return;
						} catch (InterruptedException e) {}
					}
				}
			});
		m_Self = m_Channel.getLocalAddress();
	}

	private void waitForSlavesToConnect(int numSlaves) {
		while (true) {
			View view = m_Channel.getView();
			int currentNumSlaves = view.getMembers().size() - 1;
			if (currentNumSlaves >= numSlaves) {
				break;
			}
			
			try {
				Timers.stop("network");
				System.err.println("Waiting for more slaves... " + currentNumSlaves + "/" + numSlaves);
				Thread.sleep(SLEEP_TIME);
				Timers.start("network");
			} catch (InterruptedException e) {
			}
		}
		
		View view = m_Channel.getView();
		List<Address> slaveAdds = CollectionUtil.makeList(view.getMembers());
		slaveAdds.remove(m_Self);
		
		for (Address slave : slaveAdds) {
			Message channelMsg = new Message(slave, m_Self, Null.INSTANCE);
			try {
				m_Channel.send(channelMsg);
			} catch (ChannelNotConnectedException e) {
				throw new RuntimeException(e);
			} catch (ChannelClosedException e) {
				throw new RuntimeException(e);
			}
		}
	}	

	@SuppressWarnings("unchecked")
	private void getSlaveData(int numSlaves) {
		Map<PackageID, MultiValuedMap<PackageID, Concept>> allExternalConcepts = CollectionUtil.makeMap();
		
		while (allExternalConcepts.size() != numSlaves) {
			Message msg = takeOneMessage();
			Address src = msg.getSrc();
			Object obj = msg.getObject();
			if (obj instanceof PackageID) {
				PackageID packageID = (PackageID) obj;
				m_Slaves.add(src, packageID);
			} else {
				MultiValuedMap<PackageID, Concept> externalConcepts = (MultiValuedMap<PackageID, Concept>) obj;
				PackageID packageID = m_Slaves.getB(src);
				allExternalConcepts.put(packageID, externalConcepts);
			}
		}
		
		m_Tableaux = new Ring<PackageID>(allExternalConcepts.keySet());
		m_SyncMan = new SyncManager(this, m_Tableaux);
		m_ImportGraph = new ImportGraph(allExternalConcepts);
	}

	private void initMaster(Query query) {
		m_InterTableauMan = new InterTableauManager(this, m_ImportGraph);
		m_ClashCauses = CollectionUtil.makeSet();
		m_MessageProcessor = new TableauMessageProcessorImpl();
		m_Result = new QueryResult();
		m_Result.setQuery(query);
	}

	private void startExpansion(Query query) throws ChannelNotConnectedException, ChannelClosedException {
		PackageID witnessID = query.getWitnessID();
		send(witnessID, new MakeGlobalRoot(query.getSatConcept()));
		
		for (PackageID otherID : m_Tableaux) {
			if (otherID.equals(witnessID)) continue;
			send(otherID, Null.INSTANCE);
		}
		
		m_State = State.EXPAND;
	}
	
	private void resume() {
		Profiler.INSTANCE.countClash();	
		
		BranchPointSet clashCause = Collections.min(m_ClashCauses, BranchPointSet.ORDER_BY_LATEST_BRANCH_POINT);
		m_ClashCauses.clear();
		if (clashCause.isEmpty()) {
			exitWithResult(false);
			return;
		}
		
		m_InterTableauMan.pruneTo(clashCause.getLatestBranchPoint());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("All prunings completed, resuming expansion.");
		}
		
		//Make sure all tableaux are pruned, especially if the messages processed during synchronization
		//depend on some branch on or after the clashCause
		broadcast(new Clash(clashCause));
		broadcast(new ResumeExpansion(clashCause));
		m_State = State.EXPAND;
	}

	private void exitWithResult(boolean result) {
		m_Result.setIsSatisfiable(result);
		broadcast(Exit.INSTANCE);
		m_State = State.EXIT;
	}

	private void waitForSlavesToDisconnect() {
		while (true) {
			View view = m_Channel.getView();
			int numSlaves = view.getMembers().size() - 1;
			if (numSlaves == 0) {
				break;
			}
			
			try {
				Timers.stop("network");
				System.err.println("Waiting for slaves to disconnect... " + numSlaves);
				Thread.sleep(SLEEP_TIME);
				Timers.start("network");
			} catch (InterruptedException e) {
			}
		}
	}

	
	private class TableauMessageProcessorImpl implements TableauMasterMessageProcessor {

		@Override
		public void process(Clash msg) {
			BranchPointSet clashCause = msg.getCause();
			if (m_ClashCauses.add(clashCause)) {
				//Send clash messages early so tableaux can prepare for synchronization
				broadcast(msg);
				m_State = State.CLASH;
			}
		}

		@Override
		public void process(ForwardConceptReport msg) {
			m_InterTableauMan.processConceptReport(msg);
		}

		@Override
		public void process(BackwardConceptReport msg) {
			m_InterTableauMan.processConceptReport(msg);
		}

		@Override
		public void process(BranchTokenMessage msg) {
			if (m_State == State.CLASH) return;

			PackageID nextPackageID = m_Tableaux.getNext(msg.getPackageID());
			send(nextPackageID, msg);
		}

		@Override
		public void process(SyncPing msg) {
			m_SyncMan.receiveResponse(msg);
		}
		
	}
	
}
