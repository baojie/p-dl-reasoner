package edu.iastate.pdlreasoner.model;

import java.util.Arrays;

public abstract class SetOp extends Concept {
	
	private static final long serialVersionUID = 1L;

	protected Concept[] m_Operands;

	protected SetOp(Concept... operands) {
		m_Operands = operands;
	}
	
	public Concept[] getOperands() {
		return m_Operands;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SetOp)) return false;
		SetOp other = (SetOp) obj;
		return Arrays.equals(m_Operands, other.m_Operands); 
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(m_Operands);
	}
	
}
