package edu.iastate.pdlreasoner.model;

public abstract class Restriction extends Concept {
	
	private static final long serialVersionUID = 1L;

	protected Role m_Role;
	protected Concept m_Filler;
	
	protected Restriction(Role role, Concept filler) {
		m_Role = role;
		m_Filler = filler;
	}
	
	public Role getRole() {
		return m_Role;
	}

	public Concept getFiller() {
		return m_Filler;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Restriction)) return false;
		Restriction other = (Restriction) obj;
		return m_Role.equals(other.m_Role)
			&& m_Filler.equals(other.m_Filler);
	}
	
	@Override
	public int hashCode() {
		return m_Role.hashCode() ^ m_Filler.hashCode();
	}
	
}
