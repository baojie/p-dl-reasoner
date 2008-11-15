package edu.iastate.pdlreasoner.tableau.message;

import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;

public class Clash implements Message {

	private BranchPointSet m_Cause;

	public Clash(BranchPointSet cause) {
		m_Cause = cause;
	}
	
	public BranchPointSet getCause() {
		return m_Cause;
	}

	@Override
	public void execute(MessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}
	
}
