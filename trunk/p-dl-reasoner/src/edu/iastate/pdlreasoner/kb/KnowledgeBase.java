package edu.iastate.pdlreasoner.kb;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.ModelFactory;

public class KnowledgeBase {

	private DLPackage m_Package;
	private TBox m_TBox;
	
	public KnowledgeBase(DLPackage homePackage) {
		m_Package = homePackage; 
		m_TBox = new TBox(this);
	}
	
	public DLPackage getPackage() {
		return m_Package;
	}
	
	public void addAxiom(Concept sub, Concept sup) {
		if (sub.equals(sup)) return;
		
		m_TBox.addAxiom(ModelFactory.makeSub(sub, sup));
	}
	
}
