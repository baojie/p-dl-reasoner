package edu.iastate.pdlreasoner.tableau.message;

import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;

public class Clash implements TableauMessage {

	private static final long serialVersionUID = 1L;
	
	private BranchPointSet m_Cause;

	public Clash(BranchPointSet cause) {
		m_Cause = cause;
	}
	
	public BranchPointSet getCause() {
		return m_Cause;
	}

	@Override
	public void execute(TableauMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}
	
}
