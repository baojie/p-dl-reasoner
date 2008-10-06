package edu.iastate.pdlreasoner.model;

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
