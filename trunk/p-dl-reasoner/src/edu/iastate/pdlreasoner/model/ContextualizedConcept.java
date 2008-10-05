package edu.iastate.pdlreasoner.model;

public abstract class ContextualizedConcept implements Concept {
	protected Package m_Context;
		
	protected ContextualizedConcept(Package context) {
		m_Context = context;
	}
}
