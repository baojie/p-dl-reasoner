package edu.iastate.pdlreasoner.model;

public class Not extends ContextualizedConcept {
	
	protected Concept m_Concept;
	
	protected Not(DLPackage context, Concept concept) {
		super(context);
		m_Concept = concept;
	}

	@Override
	public void accept(ConceptVisitor visitor) {
		visitor.visit(this);
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Not) || !super.equals(obj)) return false;
		Not other = (Not) obj;
		return m_Concept.equals(other.m_Concept);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() ^ m_Concept.hashCode();
	}
	
}
