package edu.iastate.pdlreasoner.kb;

public class QueryResult {

	private Query m_Query;
	private boolean m_IsSat;

	public void setQuery(Query query) {
		m_Query = query;
	}
	
	public void setIsSatisfiable(boolean v) {
		m_IsSat = v;
	}

	public boolean isSatisfiable() {
		return m_IsSat;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
			.append("Query: ")
			.append(m_Query.getQuery())
			.append("\nWitness package: ")
			.append(m_Query.getWitnessID())
			.append("\nAnswer: ")
			.append(String.valueOf(!m_IsSat)).toString();
	}
	
}
