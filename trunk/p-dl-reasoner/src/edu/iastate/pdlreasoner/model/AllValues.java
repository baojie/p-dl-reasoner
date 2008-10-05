package edu.iastate.pdlreasoner.model;

public class AllValues extends RoleOp {

	protected AllValues(Role role, Concept concept) {
		super(role, concept);
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
