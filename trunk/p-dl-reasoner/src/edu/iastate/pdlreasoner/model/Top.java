package edu.iastate.pdlreasoner.model;

import edu.iastate.pdlreasoner.model.visitor.ConceptVisitor;

public class Top extends ContextualizedConcept {

	private static final long serialVersionUID = 1L;

	protected Top(PackageID homePackageID) {
		super(homePackageID);
	}
	
	@Override
	public void accept(ConceptVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Top) && super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
