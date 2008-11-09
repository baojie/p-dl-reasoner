package edu.iastate.pdlreasoner.tableau.branch;

import java.util.BitSet;
import java.util.Comparator;

import edu.iastate.pdlreasoner.tableau.TracedConcept;

public class BranchPointSet {
	
	private BitSet m_BranchPoints;

	public static final BranchPointSet EMPTY = new BranchPointSet();
	public static final Comparator<BranchPointSet> ORDER_BY_LATEST_BRANCH_POINT = new Comparator<BranchPointSet>() {
			@Override
			public int compare(BranchPointSet o1, BranchPointSet o2) {
				return 0;
			}
		};

	public static BranchPointSet union(BranchPointSet... bpss) {
		BranchPointSet union = null;
		if (bpss.length == 1) {
			union = bpss[0];
		} else {
			union = new BranchPointSet();
			for (BranchPointSet bps : bpss) {
				union.union(bps);
			}
		}
		return union;
	}			
		
	public static BranchPointSet unionDependencies(TracedConcept... tcs) {
		BranchPointSet allDepends = null;
		if (tcs.length == 1) {
			allDepends = tcs[0].getDependency();
		} else {
			allDepends = new BranchPointSet();
			for (TracedConcept tc : tcs) {
				allDepends.union(tc.getDependency());
			}
		}
		return allDepends;
	}
	
	public BranchPointSet() {
		m_BranchPoints = new BitSet();
	}
	
	public BranchPointSet(BranchPoint bp) {
		this();
		m_BranchPoints.set(bp.getIndex());
	}

	public void union(BranchPointSet o) {
		m_BranchPoints.or(o.m_BranchPoints);
	}
	
	public void remove(BranchPoint bp) {
		m_BranchPoints.clear(bp.getIndex());
	}

	public boolean hasSameOrAfter(BranchPoint restoreTarget) {
		return false;
	}

	public BranchPoint getLatestBranchPoint() {
		return new BranchPoint(m_BranchPoints.length() - 1);
	}

	public boolean isEmpty() {
		return m_BranchPoints.isEmpty();
	}

}
