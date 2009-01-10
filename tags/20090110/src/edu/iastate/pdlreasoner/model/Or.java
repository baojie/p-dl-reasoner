package edu.iastate.pdlreasoner.model;

import edu.iastate.pdlreasoner.model.visitor.ConceptVisitor;

public class Or extends SetOp {

	protected Or(Concept... operands) {
		super(operands);
	}

	@Override
	public void accept(ConceptVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Or) && super.equals(obj);
	}

	@Override
	public int hashCode() {
		return ~super.hashCode();
	}
	
}
