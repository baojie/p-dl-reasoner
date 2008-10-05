package edu.iastate.pdlreasoner.model;

public class Not implements Concept {
	protected Package m_Context;
	protected Concept m_Concept;
	
	protected Not(Package context, Concept concept) {
		m_Context = context;
		m_Concept = concept;
	}

	@Override
	public void accept(ConceptVisitor visitor) {
		visitor.visit(this);
	}	
}
