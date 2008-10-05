package edu.iastate.pdlreasoner.model;

public abstract class SetOp implements Concept {
	protected Concept[] m_Concepts;

	protected SetOp(Concept[] concepts) {
		m_Concepts = concepts;
	}
}
