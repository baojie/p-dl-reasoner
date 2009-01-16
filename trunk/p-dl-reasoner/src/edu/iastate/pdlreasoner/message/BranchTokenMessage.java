package edu.iastate.pdlreasoner.message;

import edu.iastate.pdlreasoner.tableau.branch.BranchToken;

public class BranchTokenMessage implements MessageToSlave, MessageToMaster {

	private static final long serialVersionUID = 1L;
	
	private final BranchToken m_Token;
	
	public BranchTokenMessage(BranchToken token) {
		m_Token = token;
	}

	public BranchToken getToken() {
		return m_Token;
	}

	@Override
	public void execute(TableauSlaveMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}

	@Override
	public void execute(TableauMasterMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}
	
}
