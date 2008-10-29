package edu.iastate.pdlreasoner.tableau.messaging;

import edu.iastate.pdlreasoner.tableau.BranchPoint;

public class Clash implements Message {

	private BranchPoint m_RestoreTarget;

	@Override
	public void execute(MessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}
	
}
