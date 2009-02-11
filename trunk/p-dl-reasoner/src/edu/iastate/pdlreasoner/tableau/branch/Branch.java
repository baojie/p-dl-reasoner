package edu.iastate.pdlreasoner.tableau.branch;

import org.apache.log4j.Logger;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.tableau.TracedConcept;
import edu.iastate.pdlreasoner.tableau.TracedConceptSet;
import edu.iastate.pdlreasoner.tableau.graph.Node;

public class Branch {

	private static final Logger LOGGER = Logger.getLogger(Branch.class);
	
	//Constants
	private Node m_Node;
	private TracedConcept m_Concept;
	private BranchPoint m_ThisPoint;
	
	//Variables
	private int m_NextChoice;
	
	//Caches
	private BranchPointSet m_ThisPointSet;
	private Concept[] m_Disjuncts;
	private BranchPointSet[] m_DisjunctClashCause;
	
	public Branch(Node node, TracedConcept tc, BranchPoint bp) {
		m_Node = node;
		m_Concept = tc;
		m_ThisPoint = bp;
		m_NextChoice = 0;
		
		m_ThisPointSet = new BranchPointSet(bp);
		Or or = (Or) tc.getConcept();
		m_Disjuncts = or.getOperands();
		m_DisjunctClashCause = new BranchPointSet[m_Disjuncts.length];
	}
	
	public BranchPoint getBranchPoint() {
		return m_ThisPoint;
	}

	public BranchPointSet getDependency() {
		return m_Concept.getDependency();
	}
	
	public void tryNext() {
		BranchPointSet dependency = null;
		if (m_NextChoice == m_Disjuncts.length - 1) {
			m_DisjunctClashCause[m_NextChoice] = m_Concept.getDependency();
			dependency = BranchPointSet.union(m_DisjunctClashCause);
			dependency.remove(m_ThisPoint);
		} else {
			dependency = m_ThisPointSet;
		}
		
		TracedConcept tracedDisjunct = new TracedConcept(m_Disjuncts[m_NextChoice], dependency);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Trying next choice " + tracedDisjunct + " on branch " + this);
		}

		m_Node.addLabel(tracedDisjunct);
		m_NextChoice++;
	}

	public void setLastClashCause(BranchPointSet clashCause) {
		m_DisjunctClashCause[m_NextChoice - 1] = clashCause;
	}
	
	public void reopenConceptOnNode() {
		TracedConceptSet tcSet = m_Node.getLabelsFor(m_Concept.getConcept().getClass());
		tcSet.reopen(m_Concept);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Reopening branch concept " + m_Concept + " on node " + m_Node);
		}
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append("\n<")
			.append(m_Node).append(", ")
			.append(m_Concept).append(", ")
			.append(m_ThisPoint).append(", ")
			.append(m_NextChoice)
			.append(">")
			.toString();
	}
	
}