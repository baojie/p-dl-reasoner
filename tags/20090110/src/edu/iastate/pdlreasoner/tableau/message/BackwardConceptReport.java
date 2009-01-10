package edu.iastate.pdlreasoner.tableau.message;

import edu.iastate.pdlreasoner.server.graph.GlobalNodeID;
import edu.iastate.pdlreasoner.tableau.TracedConcept;

public class BackwardConceptReport extends ConceptReport {

	public BackwardConceptReport(GlobalNodeID importSource, GlobalNodeID importTarget, TracedConcept concept) {
		super(importSource, importTarget, concept);
	}
	
	@Override
	public void execute(MessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
			.append("ConceptReport(")
			.append(m_ImportSource).append(" <- ").append(m_ImportTarget)
			.append(", ")
			.append(m_Concept)
			.append(")")
			.toString();
	}

}
