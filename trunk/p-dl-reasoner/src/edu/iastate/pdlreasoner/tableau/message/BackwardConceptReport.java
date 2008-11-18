package edu.iastate.pdlreasoner.tableau.message;

import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.tableau.TracedConcept;

public class BackwardConceptReport extends ConceptReport {

	public BackwardConceptReport(DLPackage owner, DLPackage importer, int importerNodeID, TracedConcept concept) {
		super(owner, importer, concept);
		setImporterNodeID(importerNodeID);
	}
	
	@Override
	public void execute(MessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}

}
