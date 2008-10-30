package edu.iastate.pdlreasoner.tableau;

import java.util.Map;
import java.util.Set;

import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.ModelFactory;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Role;
import edu.iastate.pdlreasoner.model.visitor.ConceptVisitorAdapter;
import edu.iastate.pdlreasoner.struct.MultiValuedMap;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class Node {

	private TableauGraph m_Graph;
	private Node m_Parent;
	private MultiValuedMap<Role, Node> m_Children;
	
	private Map<Class<? extends Concept>, TracedConceptSet> m_Labels;
	private Set<Concept> m_Clashes;
	private NodeClashDetector m_ClashDetector;

	
	public static Node make(TableauGraph g) {
		return new Node(g);
	}
	
	private Node(TableauGraph graph) {
		m_Graph = graph;
		m_Children = new MultiValuedMap<Role, Node>();
		m_Labels = CollectionUtil.makeMap();
		m_Clashes = CollectionUtil.makeSet();
		m_ClashDetector = new NodeClashDetector();
	}
	
	//Graph structural methods
	
	public void accept(NodeVisitor v) {
		v.visit(this);
		for (Set<Node> childrenForRole : m_Children.values()) {
			for (Node child : childrenForRole) {
				child.accept(v);
			}
		}
	}
	
	public boolean containsChild(Role r, Concept c) {
		Set<Node> rChildren = m_Children.get(r);
		if (rChildren == null) return false;
		for (Node n : rChildren) {
			if (n.containsLabel(c)) return true;
		}
		return false;
	}
	
	public Set<Node> getChildrenWith(Role r) {
		return CollectionUtil.emptySetIfNull(m_Children.get(r));
	}
	
	public Node addChildWith(Role r, TracedConcept tc) {
		Node child = make(m_Graph);
		child.m_Parent = this;
		m_Children.add(r, child);
		child.addLabel(tc);
		return child;
	}
	
	
	//Semantic methods
	
	public <T extends Concept> boolean addLabel(TracedConcept tc) {
		TracedConceptSet labelSet = m_Labels.get(tc.getConcept().getClass());
		if (labelSet == null) {
			labelSet = new TracedConceptSet();
			m_Labels.put(tc.getConcept().getClass(), labelSet);
		}
		
		boolean hasAdded = labelSet.add(tc);
		if (hasAdded) {
			//hasClashWith(tc));
		}
		return hasAdded;
	}
	
	public boolean containsLabel(Concept c) {
		TracedConceptSet labelSet = m_Labels.get(c.getClass());
		if (labelSet == null) return false;
		return labelSet.contains(c);
	}
		
	public TracedConceptSet getLabelsFor(Class<? extends Concept> c) {
		return m_Labels.get(c);
	}
		
	public boolean isComplete() {
		for (TracedConceptSet tcSet : m_Labels.values()) {
			if (!tcSet.isComplete()) return false;
		}
		return true;
	}
	
	public boolean hasClash() {
		return !m_Clashes.isEmpty();
	}
	
	
	private boolean hasClashWith(Concept c) {
		c.accept(m_ClashDetector.reset());
		return m_ClashDetector.hasClash();
	}
	
	//Only NNF Concepts
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
