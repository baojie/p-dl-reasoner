package edu.iastate.pdlreasoner.model;

public class And extends SetOp {

	protected And(Concept[] concepts) {
		super(concepts);
	}

	@Override
	public void accept(ConceptVisitor visitor) {
		visitor.visit(this);
	}

}
