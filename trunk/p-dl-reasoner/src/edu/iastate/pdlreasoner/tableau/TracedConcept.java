package edu.iastate.pdlreasoner.tableau;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.visitor.ConceptVisitor;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;


public class TracedConcept {

	private Concept m_Concept;
	private BranchPointSet m_Dependency;
	
	public TracedConcept(Concept c, BranchPointSet depends) {
		m_Concept = c;
		m_Dependency = depends;
	}
	
	public static TracedConcept makeOrigin(Concept c) {
		return new TracedConcept(c, BranchPointSet.EMPTY);
	}
	
	public TracedConcept derive(Concept c) {
		return new TracedConcept(c, m_Dependency);
	}
	
	public Concept getConcept() {
		return m_Concept;
	}
	
	public BranchPointSet getDependency() {
		return m_Dependency;
	}

	public void accept(ConceptVisitor visitor) {
		m_Concept.accept(visitor);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TracedConcept)) return false;
		TracedConcept o = (TracedConcept) obj;
		return m_Concept.equals(o.m_Concept) && m_Dependency.equals(o.m_Dependency);
	}
	
	@Override
	public int hashCode() {
		return m_Concept.hashCode() ^ m_Dependency.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("<")
			.append(m_Concept)
			.append(", ")
			.append(m_Dependency)
			.append(">");
		return builder.toString();
	}
}
