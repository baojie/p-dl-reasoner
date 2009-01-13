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
	
	//Variables
	private Channel m_Channel;
	private BlockingQueue<Message> m_MessageQueue;
	private State m_State;
	
	public TableauMaster() {
		m_MessageQueue = new LinkedBlockingQueue<Message>();
		m_State = State.INIT;
	}
	
	public QueryResult run(Query query) throws ChannelException, NotEnoughSlavesException, InterruptedException {
		initChannel();
		connectWithSlaves(query);
		
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
		Address masterAdd = m_Channel.getLocalAddress();
		List<Address> members = CollectionUtil.makeList(view.getMembers());
		members.remove(masterAdd);
		if (members.size() < query.getKBs().size()) {
			m_Channel.close();
			throw new NotEnoughSlavesException("Ontology has "
					+ query.getKBs().size() + " packages but only "
					+ members.size() + " slaves are available.");
		}

		for (int i = 0; i < query.getKBs().size(); i++) {
			Message msg = new Message(members.get(i), masterAdd, query.getKBs().get(i).getPackage());
			m_Channel.send(msg);
		}
	}
	
	private void receive(Message msg) {
	}

}
