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
	private boolean m_HasClash;
	
	private Node() {
		m_Children = CollectionUtil.makeList();
		m_OpenLabels = CollectionUtil.makeSet();
		m_ExpandedLabels = CollectionUtil.makeSet();
	}
	
	public boolean addLabel(Concept c) {
		if (m_ExpandedLabels.contains(c)) return false;
		return m_OpenLabels.add(c); 
	}
	
	public boolean isComplete() {
		return m_OpenLabels.isEmpty();
	}
	
	public boolean hasClash() {
		return m_HasClash;
	}
	
	public void accept(NodeVisitor v) {
		v.visit(this);
		for (Node child : m_Children) {
			child.accept(v);
		}
	}
	
	public static Node make() {
		return new Node();
	}
}
