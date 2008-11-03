package edu.iastate.pdlreasoner.tableau;

import edu.iastate.pdlreasoner.model.DLPackage;

public class BranchPoint implements Comparable<BranchPoint> {
	
	public static final BranchPoint ORIGIN = new BranchPoint();

	private int m_Time;
	private DLPackage m_Package;
	private int m_BranchIndex;

	private BranchPoint() {}
	
	public BranchPoint(int time, DLPackage dlPackage, int branchIndex) {
		m_Time = time;
		m_Package = dlPackage;
		m_BranchIndex = branchIndex;
	}

	public int getTime() {
		return m_Time;
	}
	
	public DLPackage getPackage() {
		return m_Package;
	}
	
	public int getBranchIndex() {
		return m_BranchIndex;
	}
	
	public boolean beforeOrEquals(BranchPoint bp) {
		return compareTo(bp) <= 0;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BranchPoint)) return false;
		BranchPoint o = (BranchPoint) obj;
		if (this == ORIGIN) {
			return o == ORIGIN;
		} else {
			if (o == ORIGIN) {
				return false;
			} else {
				return m_Time == o.m_Time &&
					m_Package.equals(o.m_Package) &&
					m_BranchIndex == o.m_BranchIndex;
			}
		}
	}
	
	@Override
	public int hashCode() {
		if (this == ORIGIN) {
			return 0;
		} else {
			return (m_Time << 8) ^ m_Package.hashCode() ^ m_BranchIndex;
		}
	}
	
	@Override
	public int compareTo(BranchPoint o) {
		if (this == ORIGIN) {
			return (o == ORIGIN) ? 0 : -1;
		} else {
			if (o == ORIGIN) return 1;
			
			int timeDiff = m_Time - o.m_Time;
			if (timeDiff != 0) return timeDiff;
			
			//Both in the same package
			return m_BranchIndex - o.m_BranchIndex;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("(")
			.append(m_Time)
			.append(",")
			.append(m_Package)
			.append(",")
			.append(m_BranchIndex)
			.append(")");
		return builder.toString();
	}

}
