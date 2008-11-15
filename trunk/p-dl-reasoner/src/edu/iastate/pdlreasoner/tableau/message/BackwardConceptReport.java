package edu.iastate.pdlreasoner.tableau.message;

import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.tableau.TracedConcept;

public class BackwardConceptReport extends ConceptReport {

	public BackwardConceptReport(DLPackage source, int nodeID, TracedConcept concept) {
		super(source, nodeID, concept);
	}
	
	@Override
	public void execute(MessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}

}
