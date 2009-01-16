package edu.iastate.pdlreasoner.master;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelClosedException;
import org.jgroups.ChannelException;
import org.jgroups.ChannelNotConnectedException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import edu.iastate.pdlreasoner.exception.NotEnoughSlavesException;
import edu.iastate.pdlreasoner.kb.ImportGraph;
import edu.iastate.pdlreasoner.kb.OntologyPackage;
import edu.iastate.pdlreasoner.kb.Query;
import edu.iastate.pdlreasoner.kb.QueryResult;
import edu.iastate.pdlreasoner.message.BackwardConceptReport;
import edu.iastate.pdlreasoner.message.BranchTokenMessage;
import edu.iastate.pdlreasoner.message.Clash;
import edu.iastate.pdlreasoner.message.ForwardConceptReport;
import edu.iastate.pdlreasoner.message.MakeGlobalRoot;
import edu.iastate.pdlreasoner.message.Null;
import edu.iastate.pdlreasoner.message.TableauMasterMessageProcessor;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.net.ChannelUtil;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TableauMaster {

	private static enum State { ENTRY, EXPAND, EXIT }
	
	private BlockingQueue<Message> m_MessageQueue;
	private State m_State;
	private Channel m_Channel;
	private Address m_Self;
	
	private TableauTopology m_Tableaux;
	private InterTableauManager m_InterTableauMan;
	private Set<BranchPointSet> m_ClashCauses;
	
	//Processors
	private TableauMasterMessageProcessor m_MessageProcessor;
	
	public TableauMaster() {
		m_MessageQueue = new LinkedBlockingQueue<Message>();
		m_State = State.ENTRY;
	}
	
	public QueryResult run(Query query) throws ChannelException, NotEnoughSlavesException, InterruptedException {
		initChannel();
		connectWithSlaves(query.getOntology().getPackages());
		initMaster(query.getOntology().getImportGraph());
		startExpansion(query);
		
		while (m_State != State.EXIT) {
			m_MessageQueue.take();
			switch (m_State) {
			case EXPAND:
				
				break;
			}
		}
		
		m_Channel.close();	
		return new QueryResult(true);
	}
	
	public void send(PackageID packageID, Serializable msg) {
		Address dest = m_Tableaux.get(packageID);
		Message channelMsg = new Message(dest, m_Self, msg);
		try {
			m_Channel.send(channelMsg);
		} catch (ChannelNotConnectedException e) {
			throw new RuntimeException(e);
		} catch (ChannelClosedException e) {
			throw new RuntimeException(e);
		}
	}

	private void initChannel() throws ChannelException {
		m_Channel = new JChannel();
		m_Channel.connect(ChannelUtil.getSessionName());
		m_Channel.setReceiver(new ReceiverAdapter() {
				public void receive(Message msg) {
					while (true) {
						try {
							m_MessageQueue.put(msg);
						} catch (InterruptedException e) {
							e.printStackTrace();
							continue;
						}
					}
				}
			});
		m_Self = m_Channel.getLocalAddress();
	}

	private void connectWithSlaves(List<OntologyPackage> packages) throws NotEnoughSlavesException {
		View view = m_Channel.getView();
		List<Address> m_SlaveAdds = CollectionUtil.makeList(view.getMembers());
		m_SlaveAdds.remove(m_Self);
		if (m_SlaveAdds.size() < packages.size()) {
			m_Channel.close();
			throw new NotEnoughSlavesException("Ontology has "
					+ packages.size() + " packages but only "
					+ m_SlaveAdds.size() + " slaves are available.");
		}

		m_Tableaux = new TableauTopology(packages, m_SlaveAdds);
		
		for (PackageID packageID : m_Tableaux) {
			send(packageID, packageID);
		}
	}

	private void initMaster(ImportGraph importGraph) {
		m_InterTableauMan = new InterTableauManager(this, importGraph);
		m_ClashCauses = CollectionUtil.makeSet();
		m_MessageProcessor = new TableauMessageProcessorImpl();
	}

	private void startExpansion(Query query) throws ChannelNotConnectedException, ChannelClosedException {
		PackageID witnessID = query.getWitnessID();
		send(witnessID, new MakeGlobalRoot(query.getSatConcept()));
		
		for (PackageID otherID : m_Tableaux) {
			if (otherID.equals(witnessID)) continue;
			send(otherID, new Null());
		}
		
		m_State = State.EXPAND;
	}

	
	private class TableauMessageProcessorImpl implements TableauMasterMessageProcessor {

		@Override
		public void process(Clash msg) {
		}

		@Override
		public void process(ForwardConceptReport msg) {
		}

		@Override
		public void process(BackwardConceptReport msg) {
		}

		@Override
		public void process(BranchTokenMessage msg) {
		}
		
	}
	
}
