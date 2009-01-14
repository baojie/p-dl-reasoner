package edu.iastate.pdlreasoner.kb;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.PackageID;

public class Query {

	private Ontology m_Ontology;
	private Concept m_SatConcept;
	private PackageID m_WitnessID;
	
	public Query(Ontology ontology, Concept satConcept, PackageID witnessID) {
		m_Ontology = ontology;
		m_SatConcept = satConcept;
		m_WitnessID = witnessID;
	}
	
	public Ontology getOntology() {
		return m_Ontology;
	}

	public Concept getSatConcept() {
		return m_SatConcept;
	}

	public PackageID getWitnessID() {
		return m_WitnessID;
	}
	
}
