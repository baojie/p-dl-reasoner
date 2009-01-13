package edu.iastate.pdlreasoner.kb;

import java.util.List;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;

public class Query {

	private List<KnowledgeBase> m_KBs;
	private Concept m_SatConcept;
	private DLPackage m_Witness;
	
	public Query(List<KnowledgeBase> kbs, Concept satConcept, DLPackage witness) {
		m_KBs = kbs;
		m_SatConcept = satConcept;
		m_Witness = witness;
	}
	
	public List<KnowledgeBase> getKBs() {
		return m_KBs;
	}

	public Concept getSatConcept() {
		return m_SatConcept;
	}

	public DLPackage getWitness() {
		return m_Witness;
	}
	
}
