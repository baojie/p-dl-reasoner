package edu.iastate.pdlreasoner.model;

import edu.iastate.pdlreasoner.model.visitor.ConceptVisitor;

public class Atom extends ContextualizedConcept {
	
	private static final long serialVersionUID = 1L;

	protected String m_Fragment;
	
	protected Atom(PackageID homePackageID, String fragment) {
		super(homePackageID);
		m_Fragment = fragment;
	}

	public String getFragment() {
		return m_Fragment;
	}

	@Override
	public void accept(ConceptVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Atom) || !super.equals(obj)) return false;
		Atom other = (Atom) obj;
		return m_Fragment.equals(other.m_Fragment);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() ^ m_Fragment.hashCode();
	}
	
}
