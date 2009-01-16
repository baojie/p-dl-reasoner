package edu.iastate.pdlreasoner.tableau.branch;

import java.io.Serializable;

public class BranchToken implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
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

	@Override
	public String toString() {
		return String.valueOf(m_NextIndex);
	}
}
