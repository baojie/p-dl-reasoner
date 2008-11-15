package edu.iastate.pdlreasoner.tableau.message;

import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.tableau.TracedConcept;

public abstract class ConceptReport implements Message {
	
	protected DLPackage m_Source;
	protected int m_NodeID;
	protected TracedConcept m_Concept;
	
	protected ConceptReport(DLPackage source, int nodeID, TracedConcept concept) {
		m_Source = source;
		m_NodeID = nodeID;
		m_Concept = concept;
	}
	
	public DLPackage getSource() {
		return m_Source;
	}

	public int getNodeID() {
		return m_NodeID;
	}

	public TracedConcept getConcept() {
		return m_Concept;
	}

}
