package edu.iastate.pdlreasoner.model;

import java.net.URI;

import edu.iastate.pdlreasoner.model.visitor.ConceptVisitor;

public class Atom extends ContextualizedConcept {
	
	protected URI m_URI;
	
	protected Atom(PackageID homePackageID, URI uri) {
		super(homePackageID);
		m_URI = uri;
	}

	public URI getURI() {
		return m_URI;
	}

	@Override
	public void accept(ConceptVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Atom) || !super.equals(obj)) return false;
		Atom other = (Atom) obj;
		return m_URI.equals(other.m_URI);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() ^ m_URI.hashCode();
	}
	
}
