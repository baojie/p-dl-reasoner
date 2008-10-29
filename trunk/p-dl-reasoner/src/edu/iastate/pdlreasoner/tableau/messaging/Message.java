package edu.iastate.pdlreasoner.tableau.messaging;

public interface Message {

	void execute(MessageProcessor messageProcessor);

}