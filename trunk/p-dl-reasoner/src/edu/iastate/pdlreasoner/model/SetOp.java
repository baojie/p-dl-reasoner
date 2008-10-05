package edu.iastate.pdlreasoner.model;

import java.util.Arrays;

public abstract class SetOp implements Concept {
	
	protected Concept[] m_Concepts;

	protected SetOp(Concept[] concepts) {
		m_Concepts = concepts;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SetOp)) return false;
		SetOp other = (SetOp) obj;
		return Arrays.equals(m_Concepts, other.m_Concepts); 
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(m_Concepts);
	}
	
}
