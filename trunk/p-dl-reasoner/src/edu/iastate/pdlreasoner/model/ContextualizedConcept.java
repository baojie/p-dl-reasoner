package edu.iastate.pdlreasoner.model;

public abstract class ContextualizedConcept implements Concept {
	protected DLPackage m_Context;
		
	protected ContextualizedConcept(DLPackage context) {
		m_Context = context;
	}
}
