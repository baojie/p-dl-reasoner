package edu.iastate.pdlreasoner.tableau.message;

public interface TableauMessageProcessor {

	void process(Clash msg);
	void process(ForwardConceptReport msg);
	void process(BackwardConceptReport msg);
	void process(MakePreImage msg);
	void process(ReopenAtoms msg);
	
}
