package edu.iastate.pdlreasoner.model;

public class SomeValues extends Restriction {

	protected SomeValues(Role role, Concept filler) {
		super(role, filler);
	}

	@Override
	public void accept(ConceptVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof SomeValues) && super.equals(obj);
	}

	@Override
	public int hashCode() {
		return ~super.hashCode();
	}

}
