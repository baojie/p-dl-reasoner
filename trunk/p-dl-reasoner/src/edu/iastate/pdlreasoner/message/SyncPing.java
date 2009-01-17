package edu.iastate.pdlreasoner.message;

import edu.iastate.pdlreasoner.model.PackageID;


public class SyncPing implements MessageToSlave, MessageToMaster {

	private static final long serialVersionUID = 1L;
	
	private PackageID m_Target;
	private int m_ID;
	
	public SyncPing(PackageID target) {
		m_Target = target;
	}
	
	public PackageID getTarget() {
		return m_Target;
	}
	
	public int getID() {
		return m_ID;
	}

	public void increment() {
		m_ID++;
	}
	
	@Override
	public void execute(TableauSlaveMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}

	@Override
	public void execute(TableauMasterMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SyncPing)) return false;
		SyncPing other = (SyncPing) obj;
		return m_Target.equals(other.m_Target) && m_ID == other.m_ID;
	}
	
	@Override
	public int hashCode() {
		return m_Target.hashCode() ^ m_ID;
	}

}
