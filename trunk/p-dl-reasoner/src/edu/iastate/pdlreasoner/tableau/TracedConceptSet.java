package edu.iastate.pdlreasoner.tableau;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TracedConceptSet {

	private Map<Concept, TracedConcept> m_Open;
	private Map<Concept, TracedConcept> m_Expanded;
	
	public TracedConceptSet() {
		m_Open = CollectionUtil.makeMap();
		m_Expanded = CollectionUtil.makeMap();
	}
	
	public boolean add(TracedConcept tc) {
		Concept c = tc.getConcept();
		if (m_Expanded.containsKey(c)) return false;
		return m_Open.put(c, tc) == null;
	}
	
	public Set<TracedConcept> flush() {
		Set<TracedConcept> openCopy = CollectionUtil.copy(m_Open.values());
		m_Expanded.putAll(m_Open);
		m_Open.clear();
		return openCopy;
	}
	
	public TracedConcept getTracedConceptWith(Concept c) {
		TracedConcept tc = m_Expanded.get(c);
		if (tc != null) return tc;
		return m_Open.get(c);
	}
	
	public boolean isComplete() {
		return m_Open.isEmpty();
	}
	
	public Collection<TracedConcept> getExpanded() {
		return m_Expanded.values();
	}
}
