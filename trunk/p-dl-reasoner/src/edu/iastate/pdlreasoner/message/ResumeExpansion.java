package edu.iastate.pdlreasoner.message;

import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;

public class ResumeExpansion implements MessageToSlave {

	private static final long serialVersionUID = 1L;
	
	private BranchPointSet m_ClashCause;
	
	public ResumeExpansion(BranchPointSet clashCause) {
		m_ClashCause = clashCause;
	}

	public BranchPointSet getClashCause() {
		return m_ClashCause;
	}

	@Override
	public void execute(TableauSlaveMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}

}
