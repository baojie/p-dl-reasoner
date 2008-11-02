package edu.iastate.pdlreasoner.tableau;

import edu.iastate.pdlreasoner.model.Role;

public class Edge {

	private Node m_Parent;
	private Role m_Label;
	private Node m_Child;
	
	private Edge(Node parent, Role label, Node child) {
		m_Parent = parent;
		m_Label = label;
		m_Child = child;
	}
	
	public static Edge make(Node parent, Role label, Node child) {
		return new Edge(parent, label, child);
	}
	
	public Node getParent() {
		return m_Parent;
	}
	
	public Role getLabel() {
		return m_Label;
	}
	
	public Node getChild() {
		return m_Child;
	}
	
}
