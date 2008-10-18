package edu.iastate.pdlreasoner.tableau;

import java.util.Set;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class ABox {

	private Set<Concept> m_Roots;

	public ABox() {
		m_Roots = CollectionUtil.makeSet();
	}
	
	public void addNodeWith(Concept c) {
		
	}
	
}
