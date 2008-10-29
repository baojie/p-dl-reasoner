package edu.iastate.pdlreasoner.tableau.messaging;

public interface MessageProcessor {

	void process(Clash msg);
	void process(CPush msg);
	void process(CReport msg);
	
}
