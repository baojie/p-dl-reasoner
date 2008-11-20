package edu.iastate.pdlreasoner.server.graph;

import java.util.Map;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class InterTableauGraph {

	private SimpleDirectedGraph<GlobalNodeID,DefaultEdge> m_Graph;
	private Map<GlobalNodeID,BranchPointSet> m_NodeDependency;

	public InterTableauGraph() {
		m_Graph = new SimpleDirectedGraph<GlobalNodeID, DefaultEdge>(DefaultEdge.class);
		m_NodeDependency = CollectionUtil.makeMap();
	}

//	public boolean containsVertex(GlobalNodeID v) {
//		return false;
//	}

	public boolean addVertex(GlobalNodeID v, BranchPointSet dependency) {
		return false;
	}

	public GlobalNodeID getSourceVertexOf(GlobalNodeID v, DLPackage dlPackage) {
		return null;
	}

	public void addEdge(GlobalNodeID source, GlobalNodeID target) {
	}

}
