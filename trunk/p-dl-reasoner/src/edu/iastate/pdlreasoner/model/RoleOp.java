package edu.iastate.pdlreasoner.model;

public abstract class RoleOp implements Concept {
	protected Role m_Role;
	protected Concept m_Concept;
	
	protected RoleOp(Role role, Concept concept) {
		m_Role = role;
		m_Concept = concept;
	}
}
