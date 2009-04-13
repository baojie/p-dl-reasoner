package edu.iastate.pdlreasoner.util;


public class Profiler {

	public static Profiler INSTANCE = new Profiler();
	
	private int m_Clashes;
	private int m_Messages;
	private long m_MaxUsedMemory;
	
	private Profiler() {}
	
	public void countClash() {
		m_Clashes++;
	}
	
	public void countMessage() {
		m_Messages++;
	}
	
	public String printAll() {
		StringBuilder builder = new StringBuilder();
		builder.append("Clashes=").append(m_Clashes).append(",")
			.append("Messages=").append(m_Messages).append(",")
			.append("MaxMemory=").append(m_MaxUsedMemory).append(",");
	
		return builder.toString();
	}
	
	public void snapMemory() {
		System.gc();
		Runtime runtime = Runtime.getRuntime();
		long used = runtime.totalMemory() - runtime.freeMemory();
		if (used > m_MaxUsedMemory) {
			m_MaxUsedMemory = used;
		}
	}
	
	public long getMaxUsedMemory() {
		return m_MaxUsedMemory;
	}
	
}
