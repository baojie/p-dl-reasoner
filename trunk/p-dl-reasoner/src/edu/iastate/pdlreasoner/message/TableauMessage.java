package edu.iastate.pdlreasoner.message;

import java.io.Serializable;

public interface TableauMessage extends Serializable {

	void execute(TableauMessageProcessor messageProcessor);

}