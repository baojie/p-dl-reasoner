package edu.iastate.pdlreasoner.tableau;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

import edu.iastate.pdlreasoner.kb.Query;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.net.ChannelUtil;

public class Tableau {
	
	private static enum State { INIT, READY, EXPAND, FINAL }
	
	//Constants once set
	private Address m_MasterAdd;
	private PackageID m_AssignedPackageID;
	
	//Variables
	private Channel m_Channel;
	private BlockingQueue<Message> m_MessageQueue;
	private State m_State;
	
	public Tableau() {
		m_MessageQueue = new LinkedBlockingQueue<Message>();
		m_State = State.INIT;
	}
	
	public void run(Query query) throws ChannelException, InterruptedException {
		initChannel();
		
		while (m_State != State.FINAL) {
			receive(m_MessageQueue.take());
		}
		
		m_Channel.close();
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

	private void receive(Message msg) {
		switch (m_State) {
		case INIT:
			m_MasterAdd = msg.getSrc();
			m_AssignedPackageID = (PackageID) msg.getObject();
			
			
			
			
			m_State = State.READY;
			break;
		case READY:
			
			break;
		}
	}
	
}
