package edu.iastate.pdlreasoner.kb;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.jgrapht.Graphs;
import org.jgrapht.alg.TransitiveClosure;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.struct.MultiLabeledDirectedGraph;
import edu.iastate.pdlreasoner.struct.MultiLabeledEdge;
import edu.iastate.pdlreasoner.struct.MultiValuedMap;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class ImportGraph extends MultiLabeledDirectedGraph<PackageID,Concept> {

	private static final long serialVersionUID = 1L;

	public ImportGraph(List<OntologyPackage> packages) {
		for (OntologyPackage pack : packages) {
			PackageID homePackageID = pack.getID();
			MultiValuedMap<PackageID, Concept> externalConcepts = pack.getExternalConcepts();
			for (Entry<PackageID, Set<Concept>> entry : externalConcepts.entrySet()) {
				addLabels(entry.getKey(), homePackageID, entry.getValue());
			}
		}
		
		TransitiveClosure.INSTANCE.closeSimpleDirectedGraph(this);
	}

	public List<PackageID> getAllVerticesConnecting(PackageID source, PackageID target) {
		if (getEdge(source, target) == null) {
			throw new IllegalArgumentException("Missing an edge from the source vertex to the target vertex.");
		}
		
		List<PackageID> midVertices = Graphs.successorListOf(this, source);
		for (Iterator<PackageID> it = midVertices.iterator(); it.hasNext(); ) {
			PackageID midVertex = it.next();
			if (getEdge(midVertex, target) == null) {
				it.remove();
			}
		}
		
		return midVertices;
	}

	public List<PackageID> getImportersOf(PackageID source, Concept c) {
		if (!containsVertex(source)) return null;
		
		List<PackageID> importers = null;
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