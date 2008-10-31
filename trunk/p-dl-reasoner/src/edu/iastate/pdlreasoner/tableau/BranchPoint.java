package edu.iastate.pdlreasoner.tableau;

import edu.iastate.pdlreasoner.model.DLPackage;

public class BranchPoint implements Comparable<BranchPoint> {
	
	public static final BranchPoint ORIGIN = new BranchPoint();

	private int m_Time;
	private DLPackage m_Package;
	private int m_BranchIndex;
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BranchPoint)) return false;
		BranchPoint o = (BranchPoint) obj;
		if (this == ORIGIN && o == ORIGIN) return true;
		if (this == ORIGIN || o == ORIGIN) return false;
		return m_Time == o.m_Time &&
			m_Package.equals(o.m_Package) &&
			m_BranchIndex == o.m_BranchIndex;
	}
	
	@Override
	public int hashCode() {
		if (this == ORIGIN) {
			return 0;
		} else {
			return m_Time << 1000 ^ m_Package.hashCode() ^ m_BranchIndex;
		}
	}
	
	@Override
	public int compareTo(BranchPoint o) {
		if (this == ORIGIN && o == ORIGIN) return 0;
		if (this == ORIGIN) {
			return -1;
		} else if (o == ORIGIN) {
			return 1;
		}
		
		int timeDiff = m_Time - o.m_Time;
		if (timeDiff != 0) return timeDiff;
		
		//Both in the same package
		return m_BranchIndex - o.m_BranchIndex;
	}

}
