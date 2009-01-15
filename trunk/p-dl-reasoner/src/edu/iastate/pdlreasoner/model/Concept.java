package edu.iastate.pdlreasoner.model;

import java.io.Serializable;

import edu.iastate.pdlreasoner.model.visitor.ConceptVisitor;
import edu.iastate.pdlreasoner.model.visitor.StringRenderer;

public abstract class Concept implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		StringRenderer renderer = new StringRenderer();
		return renderer.render(this);
	}
	
	public abstract void accept(ConceptVisitor visitor);
	
}
