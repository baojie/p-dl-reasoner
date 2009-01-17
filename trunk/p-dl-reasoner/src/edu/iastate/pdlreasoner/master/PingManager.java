package edu.iastate.pdlreasoner.master;

import java.util.Map;

import edu.iastate.pdlreasoner.message.Ping;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class PingManager {

	private Map<PackageID,Ping> m_LastRequests;
	private Map<PackageID,Ping> m_LastResponses;
	
	public PingManager(TableauTopology tabs) {
		m_LastRequests = CollectionUtil.makeMap();
		m_LastResponses = CollectionUtil.makeMap();
		
		for (PackageID packageID : tabs) {
			m_LastRequests.put(packageID, new Ping(packageID));
		}
	}
	
	public void reset() {
		m_LastResponses.clear();
	}
	
	public Ping makeRequest(PackageID target) {
		Ping request = m_LastRequests.get(target);
		request.increment();
		return request;
	}
	
	public void receiveResponse(Ping response) {
		m_LastResponses.put(response.getTarget(), response);
	}
	
	public boolean haveAllPackagesResponded() {
		return m_LastRequests.equals(m_LastResponses);
	}
	
}
