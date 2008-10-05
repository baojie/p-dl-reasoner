package edu.iastate.pdlreasoner.model;

import java.net.URI;

public class Atom implements Concept {
	protected Package m_HomePackage;
	protected URI m_URI;
	
	protected Atom(Package homePackage, URI uri) {
		m_HomePackage = homePackage;
		m_URI = uri;
	}

	@Override
	public void accept(ConceptVisitor visitor) {
		visitor.visit(this);
	}
}
