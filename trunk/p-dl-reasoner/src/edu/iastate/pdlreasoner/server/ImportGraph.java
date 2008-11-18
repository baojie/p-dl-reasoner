package edu.iastate.pdlreasoner.server;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

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
}