package edu.iastate.pdlreasoner.model;

import edu.iastate.pdlreasoner.model.visitor.ConceptVisitor;

public class And extends SetOp {

	protected And(Concept... operands) {
		super(operands);
	}

	@Override
	public void accept(ConceptVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof And) && super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
}
