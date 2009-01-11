package edu.iastate.pdlreasoner.master.graph;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.struct.MultiValuedMap;
import edu.iastate.pdlreasoner.tableau.branch.BranchPoint;
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
		if (!m_Graph.containsVertex(v)) return null;
		
		for (GlobalNodeID source : Graphs.predecessorListOf(m_Graph, v)) {
			if (source.getPackage().equals(dlPackage)) return source;
		}
		return null;
	}
	
	public GlobalNodeID getTargetVertexOf(GlobalNodeID v, DLPackage dlPackage) {
		if (!m_Graph.containsVertex(v)) return null;
		
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

	public void pruneTo(BranchPoint restoreTarget) {
		for (Iterator<Entry<GlobalNodeID, BranchPointSet>> it = m_NodeDependency.entrySet().iterator(); it.hasNext(); ) {
			Entry<GlobalNodeID, BranchPointSet> entry = it.next();
			GlobalNodeID v = entry.getKey();
			BranchPointSet dependency = entry.getValue();
			if (dependency.hasSameOrAfter(restoreTarget)) {
				it.remove();
				m_Graph.removeVertex(v);
			}
		}
	}
	
	public MultiValuedMap<DLPackage,GlobalNodeID> getVerticesByPackage() {
		MultiValuedMap<DLPackage,GlobalNodeID> vertices = CollectionUtil.makeMultiValuedMap();
		for (GlobalNodeID v : m_NodeDependency.keySet()) {
			vertices.add(v.getPackage(), v);
		}
		return vertices;
	}

}
