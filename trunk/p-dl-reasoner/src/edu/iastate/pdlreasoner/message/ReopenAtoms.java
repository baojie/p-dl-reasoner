package edu.iastate.pdlreasoner.message;

import java.util.Set;

import edu.iastate.pdlreasoner.master.graph.GlobalNodeID;

public class ReopenAtoms implements MessageToSlave {
	
	private static final long serialVersionUID = 1L;

	private Set<GlobalNodeID> m_Nodes;

	public ReopenAtoms(Set<GlobalNodeID> nodes) {
		m_Nodes = nodes;
	}
	
	public Set<GlobalNodeID> getNodes() {
		return m_Nodes;
	}

	@Override
	public void execute(TableauSlaveMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}
	
}
