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

	//Graph structural fields
	private TableauGraph m_Graph;
	private Node m_Parent;
	private MultiValuedMap<Role, Node> m_Children;
	//Semantic fields
	private BranchPoint m_Dependency;
	private Map<Class<? extends Concept>, TracedConceptSet> m_Labels;
	private Set<BranchPoint> m_ClashCauses;
	private NodeClashDetector m_ClashDetector;

	
	public static Node make(TableauGraph g, BranchPoint dependency) {
		return new Node(g, dependency);
	}
	
	private Node(TableauGraph graph, BranchPoint dependency) {
		m_Graph = graph;
		m_Children = new MultiValuedMap<Role, Node>();
		m_Dependency = dependency;
		m_Labels = CollectionUtil.makeMap();
		m_ClashCauses = CollectionUtil.makeSet();
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
	
	public Node addChildBy(Role r, BranchPoint dependency) {
		Node child = make(m_Graph, dependency);
		child.m_Parent = this;
		m_Children.add(r, child);
		return child;
	}
	
	
	//Semantic methods
	
	public BranchPoint getDependency() {
		return m_Dependency;
	}
	
	public boolean addLabel(TracedConcept tc) {
		TracedConceptSet labelSet = m_Labels.get(tc.getConcept().getClass());
		if (labelSet == null) {
			labelSet = new TracedConceptSet();
			m_Labels.put(tc.getConcept().getClass(), labelSet);
		}
		
		boolean hasAdded = labelSet.add(tc);
		if (hasAdded) {
			m_ClashDetector.detect(tc);
		}
		return hasAdded;
	}
	
	public boolean containsLabel(Concept c) {
		return getTracedConceptWith(c) != null;
	}
	
	public TracedConcept getTracedConceptWith(Concept c) {
		TracedConceptSet labelSet = m_Labels.get(c.getClass());
		if (labelSet == null) return null;
		return labelSet.getTracedConceptWith(c);
	}
		
	public TracedConceptSet getLabelsFor(Class<? extends Concept> type) {
		return m_Labels.get(type);
	}
		
	public boolean isComplete() {
		for (TracedConceptSet tcSet : m_Labels.values()) {
			if (!tcSet.isComplete()) return false;
		}
		return true;
	}
	
	public Set<BranchPoint> getClashCauses() {
		return m_ClashCauses;
	}
	
	//Only NNF Concepts
	private class NodeClashDetector extends ConceptVisitorAdapter {
		
		private final DLPackage m_HomePackage;
		private TracedConcept m_Suspect;
		
		public NodeClashDetector() {
			m_HomePackage = m_Graph.getPackage();
		}
		
		public void detect(TracedConcept tc) {
			m_Suspect = tc;
			tc.accept(this);
		}

		@Override
		public void visit(Bottom bottom) {
			m_ClashCauses.add(m_Suspect.getDependency());
		}
		
		@Override
		public void visit(Atom atom) {
			Negation negatedAtom = ModelFactory.makeNegation(m_HomePackage, atom);
			TracedConcept tc = getTracedConceptWith(negatedAtom);
			if (tc != null) {
				TracedConcept maxTC = CollectionUtil.max(m_Suspect, tc);
				m_ClashCauses.add(maxTC.getDependency());
			}
		}

		@Override
		public void visit(Negation negation) {
			DLPackage context = negation.getContext();
			Concept negatedConcept = negation.getNegatedConcept();
			TracedConcept tc = getTracedConceptWith(negatedConcept);
			if (tc != null && m_HomePackage.equals(context)) {
				TracedConcept maxTC = CollectionUtil.max(m_Suspect, tc);
				m_ClashCauses.add(maxTC.getDependency());
			}
		}
	
	}
	
}
