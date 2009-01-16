package edu.iastate.pdlreasoner.message;


public interface MessageToMaster extends TableauMessage {

	void execute(TableauMasterMessageProcessor messageProcessor);

}