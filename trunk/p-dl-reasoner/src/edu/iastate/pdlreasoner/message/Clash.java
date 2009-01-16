package edu.iastate.pdlreasoner.message;

import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;

public class Clash implements MessageToSlave {

	private static final long serialVersionUID = 1L;
	
	private BranchPointSet m_Cause;

	public Clash(BranchPointSet cause) {
		m_Cause = cause;
	}
	
	public BranchPointSet getCause() {
		return m_Cause;
	}

	@Override
	public void execute(TableauSlaveMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}
	
}
