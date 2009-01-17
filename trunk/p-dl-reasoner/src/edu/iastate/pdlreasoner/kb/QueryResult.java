package edu.iastate.pdlreasoner.kb;

public class QueryResult {

	private boolean m_IsSat;

	public void setIsSatisfiable(boolean v) {
		m_IsSat = v;
	}

	public boolean isSatisfiable() {
		return m_IsSat;
	}
	
	@Override
	public String toString() {
		return String.valueOf(m_IsSat);
	}
	
}
