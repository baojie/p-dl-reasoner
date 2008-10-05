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
}
