package edu.iastate.pdlreasoner.tableau;

import java.util.List;
import java.util.Set;

import edu.iastate.pdlreasoner.model.Concept;

public class Node {

	private Set<Concept> m_OpenLabels;
	private Set<Concept> m_ExpandedLabels;
	private Node m_Parent;
	private List<Node> m_Children;
	
}
