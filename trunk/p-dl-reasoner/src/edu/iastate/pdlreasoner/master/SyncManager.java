package edu.iastate.pdlreasoner.master;

import java.io.Serializable;
import java.util.Map;

import edu.iastate.pdlreasoner.message.SyncPing;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class SyncManager {

	private TableauMaster m_TableauMaster;
	private Map<PackageID,SyncPing> m_LastRequests;
	private Map<PackageID,SyncPing> m_LastResponses;
	private boolean m_IsActive;
	
	public SyncManager(TableauMaster tableauMaster, TableauTopology tabs) {
		m_TableauMaster = tableauMaster;
		m_LastRequests = CollectionUtil.makeMap();
		m_LastResponses = CollectionUtil.makeMap();
		m_IsActive = false;
		
		for (PackageID packageID : tabs) {
			m_LastRequests.put(packageID, new SyncPing(packageID));
		}
	}
	
	public void restart() {
		m_LastResponses.clear();
		for (PackageID packageID : m_LastRequests.keySet()) {
			sendNewRequest(packageID);
		}

		m_IsActive = true;
	}
	
	public void resyncFor(PackageID destID, Serializable msg) {
		if (!m_IsActive || msg instanceof SyncPing) return;
		
		sendNewRequest(destID);
	}
	
	public void receiveResponse(SyncPing response) {
		m_LastResponses.put(response.getTarget(), response);
	}
	
	public boolean isSynchronized() {
		return m_LastRequests.equals(m_LastResponses);
	}
	
	public void stop() {
		m_IsActive = false;
	}

	private void sendNewRequest(PackageID packageID) {
		SyncPing ping = m_LastRequests.get(packageID);
		ping.increment();
		m_TableauMaster.send(packageID, ping);
	}
	
}
