package edu.iastate.pdlreasoner.tableau;

import java.util.Set;

import edu.iastate.pdlreasoner.model.AllValues;
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
	private Set<Concept> m_OpenLabels;
	private Set<Concept> m_ExpandedLabels;
	private MultiValuedMap<Role, AllValues> m_AllValuesCache;
	private Set<Concept> m_Clashes;
	private NodeClashDetector m_ClashDetector;
	
	public static Node make(TableauGraph g) {
		return new Node(g);
	}
	
	private Node(TableauGraph graph) {
		m_Graph = graph;
		m_Children = new MultiValuedMap<Role, Node>();
		m_OpenLabels = CollectionUtil.makeSet();
		m_ExpandedLabels = CollectionUtil.makeSet();
		m_AllValuesCache = new MultiValuedMap<Role, AllValues>();
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
	
	public Node addChildWith(Role r, Concept c) {
		Node child = make(m_Graph);
		child.m_Parent = this;
		m_Children.add(r, child);
		child.addLabel(c);
		return child;
	}
	
	
	//Semantic methods
	
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
	
	public Set<AllValues> getAllValuesWith(Role r) {
		return CollectionUtil.emptySetIfNull(m_AllValuesCache.get(r));
	}
	
	public Set<Concept> flushOpenLabels() {
		//SMELLY
		for (Concept c : m_OpenLabels) {
			if (c instanceof AllValues) {
				AllValues all = (AllValues) c;
				m_AllValuesCache.add(all.getRole(), all);
			}
		}
		
		Set<Concept> openCopy = CollectionUtil.copy(m_OpenLabels);
		m_ExpandedLabels.addAll(openCopy);
		m_OpenLabels.clear();
		return openCopy;
	}
	
	public boolean isComplete() {
		return m_OpenLabels.isEmpty();
	}
	
	public boolean hasClash() {
		return !m_Clashes.isEmpty();
	}
	
	private boolean hasClashWith(Concept c) {
		c.accept(m_ClashDetector.reset());
		return m_ClashDetector.hasClash();
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
