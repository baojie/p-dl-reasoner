package edu.iastate.pdlreasoner.kb;

import java.util.ArrayList;
import java.util.List;

import edu.iastate.pdlreasoner.model.Subclass;

public class TBox {

	private KnowledgeBase m_HomeKB;
	private List<Subclass> m_Axioms;
	
	public TBox(KnowledgeBase homeKB) {
		m_HomeKB = homeKB;
		m_Axioms = new ArrayList<Subclass>();
	}

	public void addAxiom(Subclass axiom) {
		m_Axioms.add(axiom);
	}
	
}
