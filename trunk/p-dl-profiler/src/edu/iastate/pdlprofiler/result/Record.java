package edu.iastate.pdlprofiler.result;

public class Record {

	double m_LoadTime;
	double m_ReasonMessageWaitTime;
	double m_WaitTime;
	double m_Clashes;
	double m_Messages;

	//Estimated
	double m_ReasonTime;

	public double getResponseTime() {
		return m_LoadTime + m_ReasonMessageWaitTime;
	}
	
	public double getReasonMessageTime() {
		return m_ReasonMessageWaitTime - m_WaitTime;
	}
	
	public double getMessageTime() {
		return getReasonMessageTime() - m_ReasonTime;
	}

	public static Record parse(String line) {
		String[] splits = line.split(",");

		Record r = new Record();
		r.m_LoadTime = parseSecond(filterColumnName(splits[1]));
		r.m_ReasonMessageWaitTime = parseSecond(filterColumnName(splits[3]));
		r.m_WaitTime = parseSecond(filterColumnName(splits[4]));
		r.m_Clashes = Integer.parseInt(filterColumnName(splits[5]));
		r.m_Messages = Integer.parseInt(filterColumnName(splits[6]));
		return r;
	}

	public Record copy() {
		Record r = new Record();
		r.m_LoadTime = m_LoadTime;
		r.m_ReasonMessageWaitTime = m_ReasonMessageWaitTime;
		r.m_WaitTime = m_WaitTime;
		r.m_Clashes = m_Clashes;
		r.m_Messages = m_Messages;
		r.m_ReasonTime = m_ReasonTime;
		return r;
	}
	
	public void add(Record r) {
		m_LoadTime += r.m_LoadTime;
		m_ReasonMessageWaitTime += r.m_ReasonMessageWaitTime;
		m_WaitTime += r.m_WaitTime;
		m_Clashes += r.m_Clashes;
		m_Messages += r.m_Messages;
		m_ReasonTime += r.m_ReasonTime;
	}
	
	public void divideBy(double q) {
		m_LoadTime /= q;
		m_ReasonMessageWaitTime /= q;
		m_WaitTime /= q;
		m_Clashes /= q;
		m_Messages /= q;
		m_ReasonTime /= q;
	}
	
	private static String filterColumnName(String column) {
		int index = column.indexOf("=");
		return column.substring(index + 1);
	}
	
	private static double parseSecond(String value) {
		return Long.parseLong(value) / 1000000000.0;
	}

}
