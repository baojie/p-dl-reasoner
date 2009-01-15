package edu.iastate.pdlreasoner.master.graph;

import java.io.Serializable;

import edu.iastate.pdlreasoner.model.PackageID;

public class GlobalNodeID implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int UNKNOWN_ID = -1;
	
	private PackageID m_PackageID;
	private boolean m_IsOrigin;
	private int m_LocalNodeID;
	
	public static GlobalNodeID make(PackageID packageID, boolean isOrigin, int localNodeID) {
		return new GlobalNodeID(packageID, isOrigin, localNodeID);
	}
	
	public static GlobalNodeID makeWithUnknownID(PackageID packageID) {
		return new GlobalNodeID(packageID);
	}

	
	private GlobalNodeID(PackageID packageID) {
		this(packageID, true, UNKNOWN_ID);
	}

	private GlobalNodeID(PackageID packageID, boolean isOrigin, int localNodeID) {
		m_PackageID = packageID;
		m_IsOrigin = isOrigin; 
		m_LocalNodeID = localNodeID;
	}

	public PackageID getPackageID() {
		return m_PackageID;
	}
	
	public boolean getIsOrigin() {
		return m_IsOrigin;
	}
	
	public int getLocalNodeID() {
		return m_LocalNodeID;
	}

	public void copyLocalIDFrom(GlobalNodeID o) {
		m_IsOrigin = o.m_IsOrigin;
		m_LocalNodeID = o.m_LocalNodeID;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GlobalNodeID)) return false;
		GlobalNodeID o = (GlobalNodeID) obj;
		return m_PackageID.equals(o.m_PackageID)
			&& m_LocalNodeID == o.m_LocalNodeID
			&& m_IsOrigin == o.m_IsOrigin;
	}
	
	@Override
	public int hashCode() {
		return m_PackageID.hashCode() ^ (m_IsOrigin ? 0 : 1) ^ m_LocalNodeID;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
			.append("(")
			.append(m_PackageID).append(", ")
			.append(m_IsOrigin).append(", ")
			.append(m_LocalNodeID == UNKNOWN_ID ? "Unknown" : m_LocalNodeID)
			.append(")")
			.toString();
	}
}
