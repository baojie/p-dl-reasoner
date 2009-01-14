package edu.iastate.pdlreasoner.master;

import java.util.List;
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
import edu.iastate.pdlreasoner.kb.Query;
import edu.iastate.pdlreasoner.kb.QueryResult;
import edu.iastate.pdlreasoner.net.ChannelUtil;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TableauMaster {

	private static enum State { INIT, EXPAND, FINAL }
	
	private BlockingQueue<Message> m_MessageQueue;
	private State m_State;
	private Channel m_Channel;
	private Address m_Master;
	private List<Address> m_Slaves;
	
	public TableauMaster() {
		m_MessageQueue = new LinkedBlockingQueue<Message>();
		m_State = State.INIT;
	}
	
	public QueryResult run(Query query) throws ChannelException, NotEnoughSlavesException, InterruptedException {
		initChannel();
		connectWithSlaves(query);
		
		m_State = State.EXPAND;
		
		while (m_State != State.FINAL) {
			receive(m_MessageQueue.take());
		}
		
		m_Channel.close();	
		return new QueryResult(true);
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
	}

	private void connectWithSlaves(Query query)	throws NotEnoughSlavesException, ChannelNotConnectedException, ChannelClosedException {
		View view = m_Channel.getView();
		m_Master = m_Channel.getLocalAddress();
		m_Slaves = CollectionUtil.makeList(view.getMembers());
		m_Slaves.remove(m_Master);
		if (m_Slaves.size() < query.getPackages().size()) {
			m_Channel.close();
			throw new NotEnoughSlavesException("Ontology has "
					+ query.getPackages().size() + " packages but only "
					+ m_Slaves.size() + " slaves are available.");
		}

		for (int i = 0; i < query.getPackages().size(); i++) {
			Message msg = new Message(m_Slaves.get(i), m_Master, query.getPackages().get(i).getID());
			m_Channel.send(msg);
		}
	}
	
	private void receive(Message msg) {
	}

}
