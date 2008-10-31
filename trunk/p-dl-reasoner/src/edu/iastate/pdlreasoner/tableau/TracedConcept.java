package edu.iastate.pdlreasoner.tableau;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.visitor.ConceptVisitor;

public class TracedConcept extends Concept implements Comparable<TracedConcept> {

	private Concept m_Concept;
	private BranchPoint m_Dependency;
	
	public TracedConcept(Concept c, BranchPoint depends) {
		m_Concept = c;
		m_Dependency = depends;
	}
	
	public static TracedConcept makeOrigin(Concept c) {
		return new TracedConcept(c, BranchPoint.ORIGIN);
	}
	
	public TracedConcept derive(Concept c) {
		return new TracedConcept(c, m_Dependency);
	}
	
	public Concept getConcept() {
		return m_Concept;
	}
	
	public BranchPoint getDependency() {
		return m_Dependency;
	}

	@Override
	public void accept(ConceptVisitor visitor) {
		m_Concept.accept(visitor);
	}

	@Override
	public boolean equals(Object o) {
		return m_Concept.equals(o);
	}
	
	@Override
	public int hashCode() {
		return m_Concept.hashCode();
	}

	@Override
	public int compareTo(TracedConcept o) {
		return m_Dependency.compareTo(o.m_Dependency);
	}
}
