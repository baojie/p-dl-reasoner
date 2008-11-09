package edu.iastate.pdlreasoner.tableau.branch;

public class BranchToken {
	
	private int m_NextIndex;

	private BranchToken(int index) {
		m_NextIndex = index;
	}
	
	public static BranchToken make() {
		return new BranchToken(0);
	}

	public static BranchToken make(BranchPoint restoreTarget) {
		return new BranchToken(restoreTarget.getIndex() + 1);
	}

	public BranchPoint makeNextBranchPoint() {
		BranchPoint bp = new BranchPoint(m_NextIndex);
		m_NextIndex++;
		return bp;
	}

}
