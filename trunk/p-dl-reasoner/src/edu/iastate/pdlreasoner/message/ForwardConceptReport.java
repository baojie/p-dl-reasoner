package edu.iastate.pdlreasoner.message;

import edu.iastate.pdlreasoner.master.graph.GlobalNodeID;
import edu.iastate.pdlreasoner.tableau.TracedConcept;

public class ForwardConceptReport extends ConceptReport {

	private static final long serialVersionUID = 1L;

	public ForwardConceptReport(GlobalNodeID importSource, GlobalNodeID importTarget, TracedConcept concept) {
		super(importSource, importTarget, concept);
	}

	@Override
	public void execute(TableauSlaveMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append("ConceptReport(")
			.append(m_ImportSource).append(" -> ").append(m_ImportTarget)
			.append(", ")
			.append(m_Concept)
			.append(")")
			.toString();
	}

}
