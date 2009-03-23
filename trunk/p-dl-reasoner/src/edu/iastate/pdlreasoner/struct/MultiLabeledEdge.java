package edu.iastate.pdlreasoner.struct;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class MultiLabeledEdge<T> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Set<T> m_Labels;
	
	public MultiLabeledEdge() {
		m_Labels = new HashSet<T>();
	}
	
	public void addLabels(Set<T> labels) {
		m_Labels.addAll(labels);
	}
	
	public Set<T> getLabels() {
		return m_Labels;
	}
	
	@Override
	public String toString() {
		return m_Labels.toString();
	}
	
}
