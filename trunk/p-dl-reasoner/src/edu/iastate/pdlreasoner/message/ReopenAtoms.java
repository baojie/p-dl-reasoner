package edu.iastate.pdlreasoner.message;

import java.util.Set;

import edu.iastate.pdlreasoner.master.graph.GlobalNodeID;

public class ReopenAtoms implements TableauMessage {
	
	private static final long serialVersionUID = 1L;

	private final Set<GlobalNodeID> m_Nodes;

	public ReopenAtoms(Set<GlobalNodeID> nodes) {
		m_Nodes = nodes;
	}
	
	public Set<GlobalNodeID> getNodes() {
		return m_Nodes;
	}

	@Override
	public void execute(TableauMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}
	
}
