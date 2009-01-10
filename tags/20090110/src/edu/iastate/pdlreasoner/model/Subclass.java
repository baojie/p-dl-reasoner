package edu.iastate.pdlreasoner.model;

public class Subclass {

	private Concept m_Sub;
	private Concept m_Sup;

	protected Subclass(Concept sub, Concept sup) {
		m_Sub = sub;
		m_Sup = sup;
	}

	public Concept getSub() {
		return m_Sub;
	}
	
	public Concept getSup() {
		return m_Sup;
	}
}
