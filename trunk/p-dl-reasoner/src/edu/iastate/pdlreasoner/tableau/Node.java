package edu.iastate.pdlreasoner.tableau;

import java.util.List;
import java.util.Set;

import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.ModelFactory;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.visitor.ConceptVisitorAdapter;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class Node {

	private TableauGraph m_Graph;
	private Node m_Parent;
	private List<Node> m_Children;
	private Set<Concept> m_OpenLabels;
	private Set<Concept> m_ExpandedLabels;
	private Set<Concept> m_Clashes;
	private NodeClashDetector m_ClashDetector;
	
	private Node(TableauGraph graph) {
		m_Graph = graph;
		m_Children = CollectionUtil.makeList();
		m_OpenLabels = CollectionUtil.makeSet();
		m_ExpandedLabels = CollectionUtil.makeSet();
		m_Clashes = CollectionUtil.makeSet();
		m_ClashDetector = new NodeClashDetector();
	}
	
	public boolean addLabel(Concept c) {
		if (m_ExpandedLabels.contains(c)) return false;
		boolean hasAdded = m_OpenLabels.add(c);
		if (hasAdded && hasClashWith(c)) {
			m_Clashes.add(c);
		}
		return hasAdded;
	}
	
	public boolean containsLabel(Concept c) {
		return m_ExpandedLabels.contains(c) || m_OpenLabels.contains(c); 
	}
	
	public boolean isComplete() {
		return m_OpenLabels.isEmpty();
	}
	
	public boolean hasClash() {
		return !m_Clashes.isEmpty();
	}
	
	public void accept(NodeVisitor v) {
		v.visit(this);
		for (Node child : m_Children) {
			child.accept(v);
		}
	}
	
	private boolean hasClashWith(Concept c) {
		c.accept(m_ClashDetector.reset());
		return m_ClashDetector.hasClash();
	}
	
	public static Node make(TableauGraph g) {
		return new Node(g);
	}
	
	private class NodeClashDetector extends ConceptVisitorAdapter {
		
		private final DLPackage m_HomePackage;
		private boolean m_HasClash;
		
		public NodeClashDetector() {
			m_HomePackage = m_Graph.getPackage();
		}

		public NodeClashDetector reset() {
			m_HasClash = false;
			return this;
		}
		
		public boolean hasClash() {
			return m_HasClash;
		}
		
		@Override
		public void visit(Bottom bottom) {
			m_HasClash = true;
		}
		
		@Override
		public void visit(Atom atom) {
			Negation negatedAtom = ModelFactory.makeNegation(m_HomePackage, atom);
			if (containsLabel(negatedAtom)) {
				m_HasClash = true;
			}
		}

		@Override
		public void visit(Negation negation) {
			DLPackage context = negation.getContext();
			Concept negatedConcept = negation.getNegatedConcept();
			if (containsLabel(negatedConcept) && m_HomePackage.equals(context)) {
				m_HasClash = true;
			}
		}
	
	}
	
}
