package edu.iastate.pdlreasoner.model;

public class Bottom implements Concept {
	public static Bottom BOTTOM = new Bottom();
	
	private Bottom() {}

	@Override
	public void accept(ConceptVisitor visitor) {
		visitor.visit(this);
	}

}
