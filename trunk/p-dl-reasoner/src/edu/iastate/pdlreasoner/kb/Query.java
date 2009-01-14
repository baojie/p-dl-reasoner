package edu.iastate.pdlreasoner.kb;

import java.util.List;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.PackageID;

public class Query {

	private List<OntologyPackage> m_Packages;
	private Concept m_SatConcept;
	private PackageID m_WitnessID;
	
	public Query(List<OntologyPackage> packages, Concept satConcept, PackageID witnessID) {
		m_Packages = packages;
		m_SatConcept = satConcept;
		m_WitnessID = witnessID;
	}
	
	public List<OntologyPackage> getPackages() {
		return m_Packages;
	}

	public Concept getSatConcept() {
		return m_SatConcept;
	}

	public PackageID getWitnessID() {
		return m_WitnessID;
	}
	
}
