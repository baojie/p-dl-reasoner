package edu.iastate.pdlreasoner.model;

import edu.iastate.pdlreasoner.model.visitor.ConceptVisitor;

public class AllValues extends Restriction {

	private static final long serialVersionUID = 1L;

	protected AllValues(Role role, Concept filler) {
		super(role, filler);
	}

	@Override
	public void accept(ConceptVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof AllValues) && super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
