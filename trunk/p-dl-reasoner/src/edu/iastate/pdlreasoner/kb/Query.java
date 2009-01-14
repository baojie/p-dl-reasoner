package edu.iastate.pdlreasoner.kb;

import java.util.List;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;

public class Query {

	private List<OntologyPackage> m_Packages;
	private Concept m_SatConcept;
	private DLPackage m_Witness;
	
	public Query(List<OntologyPackage> packages, Concept satConcept, DLPackage witness) {
		m_Packages = packages;
		m_SatConcept = satConcept;
		m_Witness = witness;
	}
	
	public List<OntologyPackage> getPackages() {
		return m_Packages;
	}

	public Concept getSatConcept() {
		return m_SatConcept;
	}

	public DLPackage getWitness() {
		return m_Witness;
	}
	
}
