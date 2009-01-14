package edu.iastate.pdlreasoner.kb;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.ModelFactory;
import edu.iastate.pdlreasoner.struct.MultiValuedMap;
import edu.iastate.pdlreasoner.tableau.TableauManagerOld;

public class OntologyPackage {

	private DLPackage m_ID;
	private TBox m_TBox;
	
	public OntologyPackage(DLPackage id) {
		m_ID = id; 
		m_TBox = new TBox(this);
	}
	
	public DLPackage getID() {
		return m_ID;
	}
	
	public void addAxiom(Concept sub, Concept sup) {
		if (sub.equals(sup)) return;
		
		m_TBox.addAxiom(ModelFactory.makeSub(sub, sup));
	}
	
	public void init() {
		m_TBox.init();
	}
	
	public MultiValuedMap<DLPackage, Concept> getExternalConcepts() {
		return m_TBox.getExternalConcepts();
	}

	public TBox getTBox() {
		return m_TBox;
	}
	
	public TableauManagerOld getTableau() {
		return new TableauManagerOld(this);
	}
}
