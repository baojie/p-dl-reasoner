package edu.iastate.pdlreasoner.server.graph;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class InterTableauTransitiveGraph {

	private SimpleDirectedGraph<GlobalNodeID,DefaultEdge> m_Graph;
	private Map<GlobalNodeID,BranchPointSet> m_NodeDependency;

	public InterTableauTransitiveGraph() {
		m_Graph = new SimpleDirectedGraph<GlobalNodeID, DefaultEdge>(DefaultEdge.class);
		m_NodeDependency = CollectionUtil.makeMap();
	}

	public boolean addVertex(GlobalNodeID v, BranchPointSet dependency) {
		boolean result = m_Graph.addVertex(v);
		if (result) {
			m_NodeDependency.put(v, dependency);
		}
		
		return result;
	}

	public GlobalNodeID getSourceVertexOf(GlobalNodeID v, DLPackage dlPackage) {
		for (GlobalNodeID source : Graphs.predecessorListOf(m_Graph, v)) {
			if (source.getPackage().equals(dlPackage)) return source;
		}
		return null;
	}
	
	public GlobalNodeID getTargetVertexOf(GlobalNodeID v, DLPackage dlPackage) {
		for (GlobalNodeID target : Graphs.successorListOf(m_Graph, v)) {
			if (target.getPackage().equals(dlPackage)) return target;
		}
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

	public List<DefaultEdge> addEdgeAndCloseTransitivity(GlobalNodeID source, GlobalNodeID target) {
		if (m_Graph.getEdge(source, target) != null) return Collections.emptyList();
		
		List<DefaultEdge> newEdges = CollectionUtil.makeList();
		
		List<GlobalNodeID> sourceAncestors = Graphs.predecessorListOf(m_Graph, source);
		sourceAncestors.add(source);
		List<GlobalNodeID> targetDescendents = Graphs.successorListOf(m_Graph, target);
		targetDescendents.add(target);
		for (GlobalNodeID sourceAncestor : sourceAncestors) {
			for (GlobalNodeID targetDescendent : targetDescendents) {
				DefaultEdge newEdge = m_Graph.addEdge(sourceAncestor, targetDescendent);
				if (newEdge != null) {
					newEdges.add(newEdge);
				}
			}
		}
		
		return newEdges;
	}

}
