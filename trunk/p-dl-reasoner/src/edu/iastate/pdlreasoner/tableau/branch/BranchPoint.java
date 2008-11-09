package edu.iastate.pdlreasoner.tableau.branch;


public class BranchPoint implements Comparable<BranchPoint> {
	
	public static final BranchPoint ORIGIN = new BranchPoint();

	private int m_Index;

	private BranchPoint() {}
	
	public BranchPoint(int index) {
		m_Index = index;
	}

	public int getIndex() {
		return m_Index;
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
				return m_Index == o.m_Index;
			}
		}
	}
	
	@Override
	public int hashCode() {
		if (this == ORIGIN) {
			return -1;
		} else {
			return m_Index;
		}
	}
	
	@Override
	public int compareTo(BranchPoint o) {
		if (this == ORIGIN) {
			return (o == ORIGIN) ? 0 : -1;
		} else {
			if (o == ORIGIN) return 1;
			
			return m_Index - o.m_Index;
		}
	}
	
	@Override
	public String toString() {
		if (this == ORIGIN) {
			return "ORIGIN";
		} else {
			return String.valueOf(m_Index); 
		}
	}

}
