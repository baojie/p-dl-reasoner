package edu.iastate.pdlreasoner.model;

public class Top extends ContextualizedConcept {

	protected Top(DLPackage homePackage) {
		super(homePackage);
	}
	
	@Override
	public void accept(ConceptVisitor visitor) {
		visitor.visit(this);
	}

}
