package edu.iastate.pdlreasoner.model;

public abstract class ContextualizedConcept implements Concept {
	
	protected DLPackage m_Context;
		
	protected ContextualizedConcept(DLPackage context) {
		m_Context = context;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ContextualizedConcept)) return false;
		ContextualizedConcept other = (ContextualizedConcept) obj;
		return m_Context.equals(other.m_Context);
	}
	
	@Override
	public int hashCode() {
		return m_Context.hashCode();
	}
	
}
