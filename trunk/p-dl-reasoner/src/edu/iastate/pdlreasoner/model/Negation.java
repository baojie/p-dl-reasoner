package edu.iastate.pdlreasoner.model;

public class Negation extends ContextualizedConcept {
	
	protected Concept m_NegatedConcept;
	
	protected Negation(DLPackage context, Concept negatedConcept) {
		super(context);
		m_NegatedConcept = negatedConcept;
	}

	public Concept getNegatedConcept() {
		return m_NegatedConcept;
	}

	@Override
	public void accept(ConceptVisitor visitor) {
		visitor.visit(this);
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Negation) || !super.equals(obj)) return false;
		Negation other = (Negation) obj;
		return m_NegatedConcept.equals(other.m_NegatedConcept);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() ^ m_NegatedConcept.hashCode();
	}
	
}
