package edu.iastate.pdlprofiler.result;

public class RunID {
	
	public String m_Config;
	public int m_Coverage;

	public RunID(String config, int coverage) {
		m_Config = config;
		m_Coverage = coverage;
	}

	public RunID copy() {
		return new RunID(m_Config, m_Coverage);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RunID)) return false;
		RunID id = (RunID) obj;
		return m_Config.equals(id.m_Config) && m_Coverage == id.m_Coverage;
	}
	
	@Override
	public int hashCode() {
		return m_Config.hashCode() ^ m_Coverage;
	}
}