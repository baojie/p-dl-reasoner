package edu.iastate.pdlreasoner.tableau.graph;

import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;

public class NodeFactory {

	private int m_NextID;
	private TableauGraph m_Graph;
	
	public NodeFactory(TableauGraph g) {
		m_NextID = 0;
		m_Graph = g;
	}
	
	public Node make(BranchPointSet dependency) {
		return new Node(m_NextID++, m_Graph, dependency);
	}
}
