package edu.iastate.pdlreasoner.tableau;

import java.util.Set;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TracedConceptSet {

	private Set<TracedConcept> m_Open;
	private Set<TracedConcept> m_Expanded;
	
	public TracedConceptSet() {
		m_Open = CollectionUtil.makeSet();
		m_Expanded = CollectionUtil.makeSet();
	}
	
	public boolean add(TracedConcept tc) {
		if (m_Expanded.contains(tc)) return false;
		return m_Open.add(tc);
	}
	
	public Set<TracedConcept> flush() {
		Set<TracedConcept> openCopy = CollectionUtil.copy(m_Open);
		m_Expanded.addAll(openCopy);
		m_Open.clear();
		return openCopy;
	}
	
	public boolean contains(Concept c) {
		return m_Expanded.contains(c) || m_Open.contains(c);
	}
	
	public boolean isComplete() {
		return m_Open.isEmpty();
	}
	
	public Set<TracedConcept> getExpanded() {
		return m_Expanded;
	}
}
