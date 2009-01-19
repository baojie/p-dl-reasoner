package edu.iastate.pdlreasoner.kb;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.ModelFactory;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.struct.MultiValuedMap;

public class OntologyPackage {

	private PackageID m_ID;
	private TBox m_TBox;
	
	public OntologyPackage(PackageID id) {
		m_ID = id; 
		m_TBox = new TBox(this);
	}
	
	public PackageID getID() {
		return m_ID;
	}
	
	public void addAxiom(Concept sub, Concept sup) {
		if (sub.equals(sup)) return;
		
		m_TBox.addAxiom(ModelFactory.makeSub(sub, sup));
	}
	
	public void init() {
		m_TBox.init();
	}
	
	public MultiValuedMap<PackageID, Concept> getExternalConcepts() {
		return m_TBox.getExternalConcepts();
	}

	public TBox getTBox() {
		return m_TBox;
	}
	
}
