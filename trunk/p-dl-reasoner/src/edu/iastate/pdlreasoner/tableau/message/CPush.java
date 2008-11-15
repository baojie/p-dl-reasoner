package edu.iastate.pdlreasoner.tableau.message;

public class CPush implements Message {

	@Override
	public void execute(MessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}

}
