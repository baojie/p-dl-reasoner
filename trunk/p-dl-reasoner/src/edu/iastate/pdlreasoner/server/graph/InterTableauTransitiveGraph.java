package edu.iastate.pdlreasoner.server.graph;

import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class InterTableauTransitiveGraph {

	private SimpleDirectedGraph<GlobalNodeID,DefaultEdge> m_Graph;
	private Map<GlobalNodeID,BranchPointSet> m_NodeDependency;
	private List<EdgeListener<DefaultEdge>> m_Listeners;

	public InterTableauTransitiveGraph() {
		m_Graph = new SimpleDirectedGraph<GlobalNodeID, DefaultEdge>(DefaultEdge.class);
		m_NodeDependency = CollectionUtil.makeMap();
		m_Listeners = CollectionUtil.makeList();
	}

	public boolean addVertex(GlobalNodeID v, BranchPointSet dependency) {
		return false;
	}

	public GlobalNodeID getSourceVertexOf(GlobalNodeID v, DLPackage dlPackage) {
		return null;
	}
	
	public BranchPointSet getDependency(GlobalNodeID v) {
		return m_NodeDependency.get(v);
	}
		
	public GlobalNodeID getEdgeSource(DefaultEdge e) {
		return m_Graph.getEdgeSource(e);
	}

	public GlobalNodeID getEdgeTarget(DefaultEdge e) {
		return m_Graph.getEdgeTarget(e);
	}

	public void addEdgeAndCloseTransitivity(GlobalNodeID source, GlobalNodeID target) {
		List<DefaultEdge> newEdges = CollectionUtil.makeList();
		
		
		
		fireEdgesAdded(newEdges);
	}

	public void addEdgeListener(EdgeListener<DefaultEdge> l) {
		m_Listeners.add(l);
	}
	
	public void removeEdgeListener(EdgeListener<DefaultEdge> l) {
		m_Listeners.remove(l);
	}

	private void fireEdgesAdded(List<DefaultEdge> newEdges) {
		for (EdgeListener<DefaultEdge> l : m_Listeners) {
			l.edgesAdded(newEdges);
		}
	}

}
