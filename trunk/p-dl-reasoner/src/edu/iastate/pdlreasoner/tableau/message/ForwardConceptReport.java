package edu.iastate.pdlreasoner.tableau.message;

import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.tableau.TracedConcept;

public class ForwardConceptReport extends ConceptReport {

	public ForwardConceptReport(DLPackage owner, int ownerNodeID, DLPackage importer, TracedConcept concept) {
		super(owner, importer, concept);
		setOwnerNodeID(ownerNodeID);
	}

	@Override
	public void execute(MessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}

}
