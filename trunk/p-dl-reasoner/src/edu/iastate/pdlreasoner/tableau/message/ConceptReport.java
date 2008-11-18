package edu.iastate.pdlreasoner.tableau.message;

import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.tableau.TracedConcept;

public abstract class ConceptReport implements Message {
	
	protected DLPackage m_Owner;
	protected DLPackage m_Importer;
	protected TracedConcept m_Concept;
	protected int m_OwnerNodeID;
	protected int m_ImporterNodeID;
	
	protected ConceptReport(DLPackage owner, DLPackage importer, TracedConcept concept) {
		m_Owner = owner;
		m_Importer = importer;
		m_Concept = concept;
		m_OwnerNodeID = -1;
		m_ImporterNodeID = -1;
	}
	
	public DLPackage getOwner() {
		return m_Owner;
	}
	
	public DLPackage getImporter() {
		return m_Importer;
	}

	public TracedConcept getConcept() {
		return m_Concept;
	}

	public void setOwnerNodeID(int id) {
		m_OwnerNodeID = id;
	}
	
	public int getOwnerNodeID() {
		return m_OwnerNodeID;
	}

	public void setImporterNodeID(int id) {
		m_ImporterNodeID = id;
	}
	
	public int getImporterNodeID() {
		return m_ImporterNodeID;
	}

}
