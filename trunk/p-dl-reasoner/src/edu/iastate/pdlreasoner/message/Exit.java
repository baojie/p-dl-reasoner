package edu.iastate.pdlreasoner.message;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Exit implements MessageToSlave {

	public static final Exit INSTANCE = new Exit();
	
	private static final long serialVersionUID = 1L;

	private Exit() {}
	
	@Override
	public void execute(TableauSlaveMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
