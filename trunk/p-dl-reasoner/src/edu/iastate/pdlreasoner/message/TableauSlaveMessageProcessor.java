package edu.iastate.pdlreasoner.message;

public interface TableauSlaveMessageProcessor {

	void process(Clash msg);
	void process(ForwardConceptReport msg);
	void process(BackwardConceptReport msg);
	void process(MakePreImage msg);
	void process(ReopenAtoms msg);
	void process(MakeGlobalRoot msg);
	void process(Null msg);
	void process(BranchTokenMessage msg);
	void process(Exit msg);
	void process(Ping msg);
	
}
