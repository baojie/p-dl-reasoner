package edu.iastate.pdlreasoner.tableau.message;

import edu.iastate.pdlreasoner.master.graph.GlobalNodeID;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;

public class MakePreImage implements TableauMessage {

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
	public void execute(TableauMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}
	
}
