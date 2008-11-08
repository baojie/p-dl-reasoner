package edu.iastate.pdlreasoner.tableau.branch;

public class Clock {
	
	private int m_Time;
	
	public int getTime() {
		return m_Time;
	}
	
	public void setTime(int t) {
		m_Time = t;
	}
	
	public void copy(Clock c) {
		m_Time = c.m_Time;
	}
	
	public void tick() {
		m_Time++;
	}
	
}
