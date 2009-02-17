package edu.iastate.pdlreasoner.kb;

import org.apache.log4j.Logger;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.ModelFactory;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.model.Subclass;
import edu.iastate.pdlreasoner.struct.MultiValuedMap;

public class OntologyPackage {

	private static final Logger LOGGER = Logger.getLogger(OntologyPackage.class);
	
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
		
		Subclass axiom = ModelFactory.makeSub(sub, sup);
		m_TBox.addAxiom(axiom);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(m_ID.toStringWithBracket() + "Added axiom: " + axiom);
		}
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
