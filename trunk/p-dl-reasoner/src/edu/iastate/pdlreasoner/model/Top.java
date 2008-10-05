package edu.iastate.pdlreasoner.model;

public class Top implements Concept {
	protected Package m_HomePackage;
	
	protected Top(Package homePackage) {
		m_HomePackage = homePackage;
	}
	
	@Override
	public void accept(ConceptVisitor visitor) {
		visitor.visit(this);
	}

}
