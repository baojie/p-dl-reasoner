package edu.iastate.pdlreasoner.tableau.messaging;

import edu.iastate.pdlreasoner.tableau.branch.BranchPoint;

public class Clash implements Message {

	private BranchPoint m_RestoreTarget;

	public Clash(BranchPoint bp) {
		m_RestoreTarget = bp;
	}
	
	public BranchPoint getRestoreTarget() {
		return m_RestoreTarget;
	}

	@Override
	public void execute(MessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}
	
}
