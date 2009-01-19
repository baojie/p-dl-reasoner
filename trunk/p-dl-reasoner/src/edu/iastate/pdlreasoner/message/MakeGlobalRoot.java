package edu.iastate.pdlreasoner.message;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import edu.iastate.pdlreasoner.model.Concept;

public class MakeGlobalRoot implements MessageToSlave {

	private static final long serialVersionUID = 1L;
	
	private Concept m_Concept;

	public MakeGlobalRoot(Concept c) {
		m_Concept = c;
	}

	public Concept getConcept() {
		return m_Concept;
	}

	@Override
	public void execute(TableauSlaveMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
