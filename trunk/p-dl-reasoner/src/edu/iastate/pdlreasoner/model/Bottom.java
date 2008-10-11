package edu.iastate.pdlreasoner.model;

import edu.iastate.pdlreasoner.model.visitor.ConceptVisitor;

public class Bottom extends Concept {
	
	public static Bottom INSTANCE = new Bottom();

	private Bottom() {
	}

	@Override
	public void accept(ConceptVisitor visitor) {
		visitor.visit(this);
	}
	
}
