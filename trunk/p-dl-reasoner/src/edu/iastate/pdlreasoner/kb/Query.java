package edu.iastate.pdlreasoner.kb;

import org.semanticweb.owl.model.OWLSubClassAxiom;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.PackageID;

public class Query {

	private OWLSubClassAxiom m_Query;
	private Concept m_SatConcept;
	private PackageID m_WitnessID;
	
	public Query(OWLSubClassAxiom query, Concept satConcept, PackageID witnessID) {
		m_Query = query;
		m_SatConcept = satConcept;
		m_WitnessID = witnessID;
	}
	
	public OWLSubClassAxiom getQuery() {
		return m_Query;
	}

	public Concept getSatConcept() {
		return m_SatConcept;
	}

	public PackageID getWitnessID() {
		return m_WitnessID;
	}
	
//	public boolean isUnderstandableByWitness() {
//		ExternalConceptsExtractor visitor = new ExternalConceptsExtractor(m_WitnessID);
//		m_SatConcept.accept(visitor);
//		
//		Set<PackageID> externals = CollectionUtil.makeSet();
//		externals.addAll(visitor.getExternalConcepts().keySet());
//		externals.addAll(visitor.getExternalNegationContexts());
//		ImportGraph importGraph = m_Ontology.getImportGraph();
//		for (PackageID external : externals) {
//			if (!importGraph.containsEdge(external, m_WitnessID)) return false;
//		}
//		
//		return true;
//	}
	
}
