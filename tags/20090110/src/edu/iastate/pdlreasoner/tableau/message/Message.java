package edu.iastate.pdlreasoner.tableau.message;

public interface Message {

	void execute(MessageProcessor messageProcessor);

}