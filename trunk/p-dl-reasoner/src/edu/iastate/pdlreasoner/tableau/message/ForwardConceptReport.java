package edu.iastate.pdlreasoner.tableau.message;

import edu.iastate.pdlreasoner.server.graph.GlobalNodeID;
import edu.iastate.pdlreasoner.tableau.TracedConcept;

public class ForwardConceptReport extends ConceptReport {

	public ForwardConceptReport(GlobalNodeID importSource, GlobalNodeID importTarget, TracedConcept concept) {
		super(importSource, importTarget, concept);
	}

	@Override
	public void execute(MessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}

}
