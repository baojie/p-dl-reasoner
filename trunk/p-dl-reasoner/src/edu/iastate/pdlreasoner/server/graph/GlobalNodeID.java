package edu.iastate.pdlreasoner.server.graph;

import edu.iastate.pdlreasoner.model.DLPackage;

public class GlobalNodeID {

	private static final int UNKNOWN_ID = -1;
	
	private DLPackage m_Package;
	private int m_LocalNodeID;
	
	public static GlobalNodeID make(DLPackage dlPackage, int localNodeID) {
		return new GlobalNodeID(dlPackage, localNodeID);
	}
	
	public static GlobalNodeID makeWithUnknownID(DLPackage dlPackage) {
		return new GlobalNodeID(dlPackage, UNKNOWN_ID);
	}

	
	private GlobalNodeID(DLPackage dlPackage, int localNodeID) {
		m_Package = dlPackage;
		m_LocalNodeID = localNodeID;
	}
	
	
	public DLPackage getPackage() {
		return m_Package;
	}

	public void copyIDFrom(GlobalNodeID o) {
		m_LocalNodeID = o.m_LocalNodeID;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GlobalNodeID)) return false;
		GlobalNodeID o = (GlobalNodeID) obj;
		return m_Package.equals(o.m_Package) && m_LocalNodeID == o.m_LocalNodeID;
	}
	
	@Override
	public int hashCode() {
		return m_Package.hashCode() ^ m_LocalNodeID;
	}
	
}
