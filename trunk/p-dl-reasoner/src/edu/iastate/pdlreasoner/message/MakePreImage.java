package edu.iastate.pdlreasoner.message;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import edu.iastate.pdlreasoner.master.graph.GlobalNodeID;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;

public class MakePreImage implements MessageToSlave {

	private static final long serialVersionUID = 1L;

	private GlobalNodeID m_GlobalNodeID;
	private BranchPointSet m_Dependency;

	public MakePreImage(GlobalNodeID globalNodeID, BranchPointSet dependency) {
		m_GlobalNodeID = globalNodeID;
		m_Dependency = dependency;
	}

	public GlobalNodeID getGlobalNodeID() {
		return m_GlobalNodeID;
	}

	public BranchPointSet getDependency() {
		return m_Dependency;
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
