package edu.iastate.pdlreasoner.master.graph;

import edu.iastate.pdlreasoner.model.PackageID;

public class GlobalNodeID {

	private static final int UNKNOWN_ID = -1;
	
	private PackageID m_PackageID;
	private int m_LocalNodeID;
	
	public static GlobalNodeID make(PackageID packageID, int localNodeID) {
		return new GlobalNodeID(packageID, localNodeID);
	}
	
	public static GlobalNodeID makeWithUnknownID(PackageID packageID) {
		return new GlobalNodeID(packageID, UNKNOWN_ID);
	}

	
	private GlobalNodeID(PackageID packageID, int localNodeID) {
		m_PackageID = packageID;
		m_LocalNodeID = localNodeID;
	}
	
	
	public PackageID getPackageID() {
		return m_PackageID;
	}

	public void copyIDFrom(GlobalNodeID o) {
		m_LocalNodeID = o.m_LocalNodeID;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GlobalNodeID)) return false;
		GlobalNodeID o = (GlobalNodeID) obj;
		return m_PackageID.equals(o.m_PackageID) && m_LocalNodeID == o.m_LocalNodeID;
	}
	
	@Override
	public int hashCode() {
		return m_PackageID.hashCode() ^ m_LocalNodeID;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
			.append("(")
			.append(m_PackageID).append(", ")
			.append(m_LocalNodeID == UNKNOWN_ID ? "Unknown" : m_LocalNodeID)
			.append(")")
			.toString();
	}
}
