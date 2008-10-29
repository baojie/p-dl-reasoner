package edu.iastate.pdlreasoner.tableau.messaging;

public class CReport implements Message {

	@Override
	public void execute(MessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}

}
