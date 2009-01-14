package edu.iastate.pdlreasoner.tableau.graph;

import org.apache.log4j.Logger;

import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;

public class NodeFactory {

	private static final Logger LOGGER = Logger.getLogger(NodeFactory.class);
	
	private int m_NextID;
	private TableauGraph m_Graph;
	
	public NodeFactory(TableauGraph g) {
		m_NextID = 0;
		m_Graph = g;
	}
	
	public Node make(BranchPointSet dependency) {
		Node n = new Node(m_NextID++, m_Graph, dependency);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(new StringBuilder()
				.append(m_Graph.getPackageID().toDebugString())
				.append("new node (")
				.append(n)
				.append(", ")
				.append(dependency)
				.append(")"));
		}
		
		return n;
	}
}
