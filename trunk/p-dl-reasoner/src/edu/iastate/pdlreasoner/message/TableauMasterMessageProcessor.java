package edu.iastate.pdlreasoner.message;

public interface TableauMasterMessageProcessor {

	void process(Clash msg);
	void process(ForwardConceptReport msg);
	void process(BackwardConceptReport msg);
	
}
