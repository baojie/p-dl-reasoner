package edu.iastate.pdlreasoner.model;

import edu.iastate.pdlreasoner.model.visitor.ConceptVisitor;
import edu.iastate.pdlreasoner.model.visitor.StringRenderer;

public abstract class Concept {
	
	@Override
	public String toString() {
		StringRenderer renderer = new StringRenderer();
		return renderer.render(this);
	}
	
	public abstract void accept(ConceptVisitor visitor);
	
}
