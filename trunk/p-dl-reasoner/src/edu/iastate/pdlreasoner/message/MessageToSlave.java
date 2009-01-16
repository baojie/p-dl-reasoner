package edu.iastate.pdlreasoner.message;


public interface MessageToSlave extends TableauMessage {

	void execute(TableauSlaveMessageProcessor messageProcessor);

}