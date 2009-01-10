package edu.iastate.pdlreasoner.tableau.message;

public interface MessageProcessor {

	void process(Clash msg);
	void process(ForwardConceptReport msg);
	void process(BackwardConceptReport msg);
	
}
