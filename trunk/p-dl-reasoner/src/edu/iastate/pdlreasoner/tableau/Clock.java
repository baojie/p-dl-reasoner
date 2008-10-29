package edu.iastate.pdlreasoner.tableau;

public class Clock {
	
	private int m_Time;
	
	public void copy(Clock c) {
		m_Time = c.m_Time;
	}
	
	public void tick() {
		m_Time++;
	}
	
	public int getTime() {
		return m_Time;
	}
	
}
