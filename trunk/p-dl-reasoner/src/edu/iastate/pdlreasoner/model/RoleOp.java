package edu.iastate.pdlreasoner.model;

public abstract class RoleOp implements Concept {
	
	protected Role m_Role;
	protected Concept m_Concept;
	
	protected RoleOp(Role role, Concept concept) {
		m_Role = role;
		m_Concept = concept;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RoleOp)) return false;
		RoleOp other = (RoleOp) obj;
		return m_Role.equals(other.m_Role)
			&& m_Concept.equals(other.m_Concept);
	}
	
	@Override
	public int hashCode() {
		return m_Role.hashCode() ^ m_Concept.hashCode();
	}
	
}
