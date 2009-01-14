package edu.iastate.pdlreasoner.master;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.jgrapht.Graphs;
import org.jgrapht.alg.TransitiveClosure;

import edu.iastate.pdlreasoner.kb.OntologyPackage;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.struct.MultiLabeledDirectedGraph;
import edu.iastate.pdlreasoner.struct.MultiLabeledEdge;
import edu.iastate.pdlreasoner.struct.MultiValuedMap;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class ImportGraph extends MultiLabeledDirectedGraph<DLPackage,Concept> {

	private static final long serialVersionUID = 1L;

	public ImportGraph(List<OntologyPackage> packages) {
		for (OntologyPackage pack : packages) {
			DLPackage homePackage = pack.getID();
			MultiValuedMap<DLPackage, Concept> externalConcepts = pack.getExternalConcepts();
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
		if (!containsVertex(source)) return null;
		
		List<DLPackage> importers = null;
		for (MultiLabeledEdge<Concept> outgoingEdge : outgoingEdgesOf(source)) {
        	if (outgoingEdge.getLabels().contains(c)) {
        		if (importers == null) {
        			//Lazy construction for optimization
        			importers = CollectionUtil.makeList();
        		}
        		
        		importers.add(getEdgeTarget(outgoingEdge));
        	}
        }

        return importers;
	}

}