package edu.iastate.pdlreasoner.tableau.branch;

import java.util.Comparator;

import edu.iastate.pdlreasoner.tableau.TracedConcept;

public class BranchPointSet {

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
	}
	
	public BranchPointSet(BranchPoint bp) {
	}

	public void union(BranchPointSet dependency) {
	}
	
	public void remove(BranchPoint bp) {
		
	}

	public boolean hasSameOrAfter(BranchPoint restoreTarget) {
		return false;
	}

	public BranchPoint getLatestBranchPoint() {
		return null;
	}

	public boolean isEmpty() {
		return false;
	}

}
