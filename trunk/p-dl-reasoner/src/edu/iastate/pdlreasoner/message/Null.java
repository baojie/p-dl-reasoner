package edu.iastate.pdlreasoner.message;

public class Null implements TableauMessage {

	private static final long serialVersionUID = 1L;

	@Override
	public void execute(TableauMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}

}
