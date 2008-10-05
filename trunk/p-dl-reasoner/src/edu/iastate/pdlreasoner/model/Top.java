package edu.iastate.pdlreasoner.model;

public class Top extends ContextualizedConcept {

	protected Top(DLPackage homePackage) {
		super(homePackage);
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
