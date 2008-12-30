package edu.iastate.pdlreasoner.server;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.jgrapht.Graphs;
import org.jgrapht.alg.TransitiveClosure;

import edu.iastate.pdlreasoner.kb.KnowledgeBase;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.struct.MultiLabeledDirectedGraph;
import edu.iastate.pdlreasoner.struct.MultiValuedMap;

public class ImportGraph extends MultiLabeledDirectedGraph<DLPackage,Concept> {

	private static final long serialVersionUID = 1L;

	public ImportGraph(List<KnowledgeBase> kbs) {
		for (KnowledgeBase kb : kbs) {
			DLPackage homePackage = kb.getPackage();
			MultiValuedMap<DLPackage, Concept> externalConcepts = kb.getExternalConcepts();
			for (Entry<DLPackage, Set<Concept>> entry : externalConcepts.entrySet()) {
				addLabels(entry.getKey(), homePackage, entry.getValue());
			}
		}
		
		TransitiveClosure.INSTANCE.closeSimpleDirectedGraph(this);
	}

	public List<DLPackage> getAllVerticesConnecting(DLPackage source, DLPackage target) {
		if (getEdge(source, target) == null) {
			throw new IllegalArgumentException("Missing an edge from the source vertex to the target vertex.");
		}
		
		List<DLPackage> midVertices = Graphs.successorListOf(this, source);
		for (Iterator<DLPackage> it = midVertices.iterator(); it.hasNext(); ) {
			DLPackage midVertex = it.next();
			if (getEdge(midVertex, target) == null) {
				it.remove();
			}
		}
		
		return midVertices;
	}

	public List<DLPackage> getImportersOf(DLPackage source, Concept c) {
		return null;
	}

}