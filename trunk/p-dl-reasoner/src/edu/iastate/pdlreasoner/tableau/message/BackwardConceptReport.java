package edu.iastate.pdlreasoner.tableau.message;

import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.tableau.TracedConcept;

public class BackwardConceptReport extends ConceptReport {

	public BackwardConceptReport(DLPackage source, DLPackage destination, int nodeID, TracedConcept concept) {
		super(source, destination, nodeID, concept);
	}
	
	@Override
	public void execute(MessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}

}
