package edu.iastate.pdlreasoner.tableau;

import java.util.List;
import java.util.Set;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class Node {

	private Node m_Parent;
	private List<Node> m_Children;
	private Set<Concept> m_OpenLabels;
	private Set<Concept> m_ExpandedLabels;
	
	private Node() {
		m_Children = CollectionUtil.makeList();
		m_OpenLabels = CollectionUtil.makeSet();
		m_ExpandedLabels = CollectionUtil.makeSet();
	}
	
	public boolean addLabel(Concept c) {
		if (m_ExpandedLabels.contains(c)) return false;
		return m_OpenLabels.add(c); 
	}
	
	public static Node make() {
		return new Node();
	}
}
