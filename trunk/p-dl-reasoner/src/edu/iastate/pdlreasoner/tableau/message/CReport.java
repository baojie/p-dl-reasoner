package edu.iastate.pdlreasoner.tableau.message;

public class CReport implements Message {

	@Override
	public void execute(MessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}

}
