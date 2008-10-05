package edu.iastate.pdlreasoner.model;

import java.net.URI;

public class Atom extends ContextualizedConcept {
	protected URI m_URI;
	
	protected Atom(DLPackage homePackage, URI uri) {
		super(homePackage);
		m_URI = uri;
	}

	@Override
	public void accept(ConceptVisitor visitor) {
		visitor.visit(this);
	}
}
