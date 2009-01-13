package edu.iastate.pdlreasoner.master;

import java.util.List;

import org.jgroups.Address;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.View;

import edu.iastate.pdlreasoner.exception.NotEnoughSlavesException;
import edu.iastate.pdlreasoner.kb.Query;
import edu.iastate.pdlreasoner.kb.QueryResult;
import edu.iastate.pdlreasoner.net.ChannelUtil;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TableauMaster {

	//Constants on construction
	private Query m_Query;

	public TableauMaster(Query query) {
		m_Query = query;
	}

	public QueryResult run() throws ChannelException, NotEnoughSlavesException {
		JChannel channel = new JChannel();
		channel.connect(ChannelUtil.getSessionName());
		View view = channel.getView();
		Address masterAdd = channel.getLocalAddress();
		List<Address> members = CollectionUtil.makeList(view.getMembers());
		members.remove(masterAdd);
		if (members.size() < m_Query.getKBs().size()) {
			channel.close();
			throw new NotEnoughSlavesException("Ontology has " + m_Query.getKBs().size() + " packages but only " + members.size() + " slaves are available.");
		}

		for (int i = 0; i < m_Query.getKBs().size(); i++) {
			Message msg = new Message(members.get(i), masterAdd, m_Query.getKBs().get(i).getPackage());
			channel.send(msg);
		}
		
		//channel.close();
		
		
		return new QueryResult(true);
	}
	

}
