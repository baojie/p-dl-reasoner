package edu.iastate.pdlreasoner.util;


public class Profiler {

	public static Profiler INSTANCE = new Profiler();
	
	private int m_Clashes;
	private int m_Messages;
	
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
			.append("Messages=").append(m_Messages).append(",");
	
		return builder.toString();
	}
	
}
