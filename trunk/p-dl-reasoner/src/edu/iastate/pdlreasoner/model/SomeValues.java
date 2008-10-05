package edu.iastate.pdlreasoner.model;

public class SomeValues extends RoleOp {

	protected SomeValues(Role role, Concept concept) {
		super(role, concept);
	}

	@Override
	public void accept(ConceptVisitor visitor) {
		visitor.visit(this);
	}

}
