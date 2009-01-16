package edu.iastate.pdlreasoner.message;

import edu.iastate.pdlreasoner.model.Concept;

public class MakeGlobalRoot implements TableauMessage {

	private static final long serialVersionUID = 1L;
	
	private final Concept m_Concept;

	public MakeGlobalRoot(Concept c) {
		m_Concept = c;
	}

	public Concept getConcept() {
		return m_Concept;
	}

	@Override
	public void execute(TableauMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}

}
