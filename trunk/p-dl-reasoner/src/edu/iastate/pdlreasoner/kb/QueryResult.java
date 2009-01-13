package edu.iastate.pdlreasoner.kb;

public class QueryResult {

	private boolean m_IsSat;

	public QueryResult(boolean isSat) {
		m_IsSat = isSat;
	}

	public boolean isSatisfiable() {
		return m_IsSat;
	}
	
	@Override
	public String toString() {
		return String.valueOf(m_IsSat);
	}
	
}
