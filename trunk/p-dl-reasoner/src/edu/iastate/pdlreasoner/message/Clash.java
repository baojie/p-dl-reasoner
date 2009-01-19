package edu.iastate.pdlreasoner.message;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;

public class Clash implements MessageToSlave, MessageToMaster {

	private static final long serialVersionUID = 1L;
	
	private BranchPointSet m_Cause;

	public Clash(BranchPointSet cause) {
		m_Cause = cause;
	}
	
	public BranchPointSet getCause() {
		return m_Cause;
	}

	@Override
	public void execute(TableauSlaveMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}

	@Override
	public void execute(TableauMasterMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
