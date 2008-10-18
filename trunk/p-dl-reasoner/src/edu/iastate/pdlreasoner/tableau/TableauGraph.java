package edu.iastate.pdlreasoner.tableau;

import java.util.Set;

import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TableauGraph {

	private Set<Node> m_Roots;

	public TableauGraph() {
		m_Roots = CollectionUtil.makeSet();
	}
	
	public Node makeRoot() {
		Node n = Node.make();
		m_Roots.add(n);
		return n;
	}
	
}
