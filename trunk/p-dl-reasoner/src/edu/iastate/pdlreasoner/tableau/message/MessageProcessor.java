package edu.iastate.pdlreasoner.tableau.message;

public interface MessageProcessor {

	void process(Clash msg);
	void process(CPush msg);
	void process(CReport msg);
	
}
