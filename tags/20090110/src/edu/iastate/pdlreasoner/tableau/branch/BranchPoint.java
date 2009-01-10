package edu.iastate.pdlreasoner.tableau.branch;


public class BranchPoint implements Comparable<BranchPoint> {
	
	private int m_Index;

	public BranchPoint(int index) {
		m_Index = index;
	}

	public int getIndex() {
		return m_Index;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BranchPoint)) return false;
		BranchPoint o = (BranchPoint) obj;
		return m_Index == o.m_Index;
	}
	
	@Override
	public int hashCode() {
		return m_Index;
	}
	
	@Override
	public int compareTo(BranchPoint o) {
		return m_Index - o.m_Index;
	}
	
	@Override
	public String toString() {
		return String.valueOf(m_Index); 
	}

}
