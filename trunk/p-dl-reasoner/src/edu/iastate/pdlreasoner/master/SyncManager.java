package edu.iastate.pdlreasoner.master;

import java.io.Serializable;
import java.util.Map;

import edu.iastate.pdlreasoner.message.BranchTokenMessage;
import edu.iastate.pdlreasoner.message.Null;
import edu.iastate.pdlreasoner.message.SyncPing;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class SyncManager {

	private TableauMaster m_TableauMaster;
	private Map<PackageID,SyncPing> m_LastRequests;
	private Map<PackageID,SyncPing> m_LastResponses;
	private boolean m_IsInVerifyingPhase;
	
	public SyncManager(TableauMaster tableauMaster, Iterable<PackageID> tabs) {
		m_TableauMaster = tableauMaster;
		m_LastRequests = CollectionUtil.makeMap();
		m_LastResponses = CollectionUtil.makeMap();
		m_IsInVerifyingPhase = false;
		
		for (PackageID packageID : tabs) {
			m_LastRequests.put(packageID, new SyncPing(packageID));
		}
	}
	
	public void restartSync() {
		m_IsInVerifyingPhase = false;
		startSyncPhase();
	}

	public void intercept(PackageID destID, Serializable msg) {
		//These do not invalidate the completeness of a tableau nor the synchronization state during a clash
		if (msg instanceof SyncPing || msg instanceof BranchTokenMessage || msg instanceof Null) return;
		
		m_IsInVerifyingPhase = false;
	}
	
	public void receiveResponse(SyncPing response) {
		m_LastResponses.put(response.getTarget(), response);
		
		if (m_LastRequests.equals(m_LastResponses) && !m_IsInVerifyingPhase) {
			m_IsInVerifyingPhase = true;
			startSyncPhase();
		}
	}
	
	public boolean isSynchronized() {
		return m_IsInVerifyingPhase && m_LastRequests.equals(m_LastResponses);
	}
	
	private void startSyncPhase() {
		m_LastResponses.clear();
		for (PackageID packageID : m_LastRequests.keySet()) {
			sendNewRequest(packageID);
		}
	}
	
	private void sendNewRequest(PackageID packageID) {
		SyncPing ping = m_LastRequests.get(packageID);
		ping.increment();
		m_TableauMaster.send(packageID, ping);
	}

}
