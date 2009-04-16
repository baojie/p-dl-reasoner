package edu.iastate.pdlprofiler.result;

public class RunRecord {

	RunID m_ID;
	
	Record m_Central;
	Record m_Master;
	Record[] m_Slaves;
	
	public static RunRecord parse(String cLine, String mLine, String[] sLine) {
		RunRecord record = new RunRecord();
		
		record.m_Central = Record.parse(cLine);
		record.m_Master = Record.parse(mLine);
		record.m_Slaves = new Record[sLine.length];
		for (int i = 0; i < sLine.length; i++) {
			record.m_Slaves[i] = Record.parse(sLine[i]);
		}
		
		record.estimateDistributedReasonMessageTime();
		return record;
	}
	
	public RunRecord copy() {
		RunRecord r = new RunRecord();
		if (m_ID != null) r.m_ID = m_ID.copy();
		r.m_Central = m_Central.copy();
		r.m_Master = m_Master.copy();
		r.m_Slaves = new Record[m_Slaves.length];
		for (int i = 0; i < m_Slaves.length; i++) {
			r.m_Slaves[i] = m_Slaves[i].copy();
		}
		
		return r;
	}
	
	public void add(RunRecord r) {
		m_Central.add(r.m_Central);
		m_Master.add(r.m_Master);
		for (int i = 0; i < m_Slaves.length; i++) {
			m_Slaves[i].add(r.m_Slaves[i]);
		}
	}
	
	public void divideBy(double q) {
		m_Central.divideBy(q);
		m_Master.divideBy(q);
		for (int i = 0; i < m_Slaves.length; i++) {
			m_Slaves[i].divideBy(q);
		}
	}

	private void estimateDistributedReasonMessageTime() {
		m_Central.m_WaitTime = 0.0;
		m_Central.m_ReasonTime = m_Central.m_ReasonMessageWaitTime;
		
		double totalDistributedReasonMessageTime = m_Master.getReasonMessageTime();
		for (int i = 0; i < m_Slaves.length; i++) {
			totalDistributedReasonMessageTime += m_Slaves[i].getReasonMessageTime();
		}
		
		double centralizedReasonTime = m_Central.m_ReasonMessageWaitTime;
		m_Master.m_ReasonTime = centralizedReasonTime * m_Master.getReasonMessageTime() / totalDistributedReasonMessageTime;
		for (int i = 0; i < m_Slaves.length; i++) {
			m_Slaves[i].m_ReasonTime = centralizedReasonTime * m_Slaves[i].getReasonMessageTime() / totalDistributedReasonMessageTime;
		}
	}

	public void setID(RunID id) {
		m_ID = id;
	}
	
	public double getCentralizedResponseTime() {
		return m_Central.getResponseTime();
	}
	
	public double getDistributedResponseTime() {
		return m_Master.getResponseTime();
	}
	
	public double getCentralizedClashes() {
		return m_Central.m_Clashes;
	}
	
	public double getDistributedClashes() {
		return m_Master.m_Clashes;
	}
	
	public double getCentralizedMessages() {
		return m_Central.m_Messages;
	}
	
	public double getDistributedMessages() {
		return m_Master.m_Messages;
	}
	
}