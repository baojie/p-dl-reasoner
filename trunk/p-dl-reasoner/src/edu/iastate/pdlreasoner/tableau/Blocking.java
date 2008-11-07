package edu.iastate.pdlreasoner.tableau;

import java.util.List;

public class Blocking {

	public boolean isBlocked(Node n) {
		List<Node> ancestors = n.getAncestors();
		
		//Direct
		for (Node anc : ancestors) {
			if (n.isSubsetOf(anc)) return true;
		}
		
		//Indirect
		for (int sub = 0; sub < ancestors.size() - 1; sub++) {
			for (int sup = sub + 1; sup < ancestors.size(); sup++) {
				Node subNode = ancestors.get(sub);
				Node supNode = ancestors.get(sup);
				if (subNode.isSubsetOf(supNode)) {
					return true;					
				}
			}
		}
		
		return false;
	}

}
