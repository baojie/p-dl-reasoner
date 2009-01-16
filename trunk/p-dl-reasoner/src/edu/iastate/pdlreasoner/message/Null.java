package edu.iastate.pdlreasoner.message;

public class Null implements MessageToSlave {

	private static final long serialVersionUID = 1L;

	@Override
	public void execute(TableauSlaveMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}

}
