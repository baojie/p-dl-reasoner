package edu.iastate.pdlreasoner.tableau;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.Or;

public class Branch {

	private Node m_Node;
	private TracedConcept m_Concept;
	private int m_NextChoice;
	private BranchPoint m_ThisPoint;
	
	private Concept[] m_Disjuncts;
	
	public Branch(Node node, TracedConcept tc) {
		m_Node = node;
		m_Concept = tc;
		m_NextChoice = 0;
		
		Or or = (Or) tc.getConcept();
		m_Disjuncts = or.getOperands();
	}

	public void setBranchPoint(BranchPoint branchPoint) {
		m_ThisPoint = branchPoint;
	}
	
	public BranchPoint getDependency() {
		return m_Concept.getDependency();
	}
	
	public boolean tryNext() {
		if (m_NextChoice >= m_Disjuncts.length) return false;
		
		TracedConcept tracedDisjunct = new TracedConcept(m_Disjuncts[m_NextChoice], m_ThisPoint);
		m_Node.addLabel(tracedDisjunct);
		m_NextChoice++;
		return true;
	}

}
