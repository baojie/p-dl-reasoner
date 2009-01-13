package edu.iastate.pdlreasoner.tableau;

import org.jgroups.Address;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

import edu.iastate.pdlreasoner.kb.Query;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.net.ChannelUtil;

public class Tableau {

	//Constants on construction
	private Query m_Query;
	
	//Constants once set
	private Address m_MasterAdd;
	private DLPackage m_AssignedPackage;
	
	public Tableau(Query query) {
		m_Query = query;
	}

	public void run() throws ChannelException {
		JChannel channel = new JChannel();
		channel.connect(ChannelUtil.getSessionName());
	
		channel.setReceiver(new ReceiverAdapter() {
				@Override
				public void receive(Message msg) {
					m_MasterAdd = msg.getSrc();
					m_AssignedPackage = (DLPackage) msg.getObject();
					
					System.out.println(m_MasterAdd);
					System.out.println(m_AssignedPackage);
				}
			});
	}
	
}
