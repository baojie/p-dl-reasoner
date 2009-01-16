package edu.iastate.pdlreasoner.tableau.graph;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import edu.iastate.pdlreasoner.master.graph.GlobalNodeID;
import edu.iastate.pdlreasoner.model.AllValues;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.model.ModelFactory;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.Role;
import edu.iastate.pdlreasoner.model.Top;
import edu.iastate.pdlreasoner.model.visitor.ConceptVisitorAdapter;
import edu.iastate.pdlreasoner.struct.MultiValuedMap;
import edu.iastate.pdlreasoner.tableau.TracedConcept;
import edu.iastate.pdlreasoner.tableau.TracedConceptSet;
import edu.iastate.pdlreasoner.tableau.branch.BranchPoint;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class Node {

	private static final Logger LOGGER = Logger.getLogger(Node.class);
	
	private boolean m_IsOrigin;
	private int m_ID;
	private GlobalNodeID m_GlobalID;
	
	//Graph structural fields
	private TableauGraph m_Graph;
	private Edge m_ParentEdge;
	private MultiValuedMap<Role, Edge> m_Children;
	
	//Semantic fields
	private BranchPointSet m_Dependency;
	private Map<Class<? extends Concept>, TracedConceptSet> m_Labels;
	private Set<BranchPointSet> m_ClashCauses;
	private NodeClashDetector m_ClashDetector;

	private Node(TableauGraph graph, BranchPointSet dependency) {
		m_Graph = graph;
		m_Children = CollectionUtil.makeMultiValuedMap();
		m_Dependency = dependency;
		m_Labels = CollectionUtil.makeMap();
		m_ClashCauses = CollectionUtil.makeSet();
		m_ClashDetector = new NodeClashDetector();
	}
	
	protected Node(int id, TableauGraph graph, BranchPointSet dependency) {
		this(graph, dependency);
		m_IsOrigin = true;
		m_ID = id;
	}
	
	protected Node(GlobalNodeID globalNodeID, TableauGraph graph, BranchPointSet dependency) {
		this(graph, dependency);
		m_IsOrigin = false;
		m_ID = globalNodeID.getLocalNodeID();
		m_GlobalID = globalNodeID;
		
		graph.put(m_GlobalID, this);
	}

	public GlobalNodeID getGlobalNodeID() {
		if (m_GlobalID == null) {
			m_GlobalID = GlobalNodeID.make(m_Graph.getPackageID(), m_IsOrigin, m_ID);
			m_Graph.put(m_GlobalID, this);
		}
		return m_GlobalID;
	}
	
	//Graph structural methods
	
	public void accept(NodeVisitor v) {
		v.visit(this);
		for (Set<Edge> childrenForRole : m_Children.values()) {
			for (Edge edge : childrenForRole) {
				edge.getChild().accept(v);
			}
		}
	}
	
	public boolean containsChild(Role r, Concept c) {
		Set<Edge> rChildren = m_Children.get(r);
		if (rChildren == null) return false;
		for (Edge edge : rChildren) {
			if (edge.getChild().containsLabel(c)) return true;
		}
		return false;
	}
	
	public Set<Edge> getChildrenWith(Role r) {
		return CollectionUtil.emptySetIfNull(m_Children.get(r));
	}
	
	public List<Node> getAncestors() {
		List<Node> ancestors = CollectionUtil.makeList();
		for (Edge e = m_ParentEdge; e != null; ) { 
			Node parent = e.getParent();
			ancestors.add(parent);
			e = parent.m_ParentEdge;
		}
		return ancestors;
	}
	
	public Node addChildBy(Role r, BranchPointSet dependency) {
		Node child = m_Graph.makeNode(dependency);
		Edge edge = Edge.make(this, r, child);
		child.m_ParentEdge = edge;
		m_Children.add(r, edge);
		return child;
	}
	
	public void removeFromParent() {
		if (m_ParentEdge == null) return;
		
		m_ParentEdge.getParent().m_Children.remove(m_ParentEdge.getLabel(), m_ParentEdge);
		m_ParentEdge = null;
	}
	
	
	//Semantic methods
	
	public BranchPointSet getDependency() {
		return m_Dependency;
	}
	
	public boolean addLabel(TracedConcept tc) {
		Concept concept = tc.getConcept();
		if (isLocalTop(concept)) return false;
		
		Class<? extends Concept> conceptClass = concept.getClass();
		TracedConceptSet labelSet = m_Labels.get(conceptClass);
		if (labelSet == null) {
			labelSet = new TracedConceptSet();
			m_Labels.put(conceptClass, labelSet);
		}
		
		boolean hasAdded = labelSet.add(tc);
		if (hasAdded) {
			m_ClashDetector.detect(tc);
		}
		return hasAdded;
	}

	private boolean isLocalTop(Concept c) {
		if (c instanceof Top) {
			Top top = (Top) c;
			return m_Graph.getPackageID().equals(top.getContext());
		}
		return false;
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
	
	public boolean isSubsetOf(Node o) {
		if (!CollectionUtil.isSubsetOf(m_Labels.keySet(), o.m_Labels.keySet())) return false;
		
		Map<Class<? extends Concept>, TracedConceptSet> oLabels = o.m_Labels;
		for (Entry<Class<? extends Concept>, TracedConceptSet> entry : m_Labels.entrySet()) {
			TracedConceptSet set = entry.getValue();
			TracedConceptSet oSet = oLabels.get(entry.getKey());
			if (!oSet.containsAllConcepts(set)) return false;
		}
		return true;
	}
		
	public boolean isComplete() {
		for (TracedConceptSet tcSet : m_Labels.values()) {
			if (!tcSet.isComplete()) return false;
		}
		return true;
	}
	
	public Set<BranchPointSet> getClashCauses() {
		return m_ClashCauses;
	}
	
	public void clearClashCauses() {
		m_ClashCauses.clear();
	}
	
	private void addClashCause(TracedConcept... clashes) {
		m_ClashCauses.add(BranchPointSet.unionDependencies(clashes));
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(m_Graph.getPackageID().toDebugString() + "clash found on node " + this + " = " + CollectionUtil.asSet(clashes));
		}
	}
	
	public void pruneAndReopenLabels(BranchPoint restoreTarget) {
		boolean hasChanged = false;
		for (Entry<Class<? extends Concept>, TracedConceptSet> entry : m_Labels.entrySet()) {
			hasChanged |= entry.getValue().prune(restoreTarget);
		}
		
		if (hasChanged) {
			for (Entry<Class<? extends Concept>, TracedConceptSet> entry : m_Labels.entrySet()) {
				if (!Or.class.equals(entry.getKey())) {
					entry.getValue().reopenAll();
				}
			}
			
			if (m_ParentEdge != null) {
				TracedConceptSet parentAllValues = m_ParentEdge.getParent().getLabelsFor(AllValues.class);
				if (parentAllValues != null) {
					parentAllValues.reopenAll();
				}
			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(m_Graph.getPackageID().toDebugString() + "pruned and reopened node " + this + ": " + getLabels());
			}
		}
	}
	
	public void reopenAtoms() {
		for (Entry<Class<? extends Concept>, TracedConceptSet> entry : m_Labels.entrySet()) {
			Class<? extends Concept> type = entry.getKey();
			if (type.equals(Atom.class) || type.equals(Top.class)) {
				entry.getValue().reopenAll();
			}
		}
	}

	//Only NNF Concepts
	private class NodeClashDetector extends ConceptVisitorAdapter {
		
		private final PackageID m_HomePackageID;
		private TracedConcept m_Suspect;
		
		public NodeClashDetector() {
			m_HomePackageID = m_Graph.getPackageID();
		}
		
		public void detect(TracedConcept tc) {
			m_Suspect = tc;
			tc.accept(this);
		}

		@Override
		public void visit(Bottom bottom) {
			addClashCause(m_Suspect);
		}
		
		@Override
		public void visit(Atom atom) {
			Negation negatedAtom = ModelFactory.makeNegation(m_HomePackageID, atom);
			TracedConcept tc = getTracedConceptWith(negatedAtom);
			if (tc != null) {
				addClashCause(m_Suspect, tc);
			}
		}

		@Override
		public void visit(Negation negation) {
			PackageID context = negation.getContext();
			Concept negatedConcept = negation.getNegatedConcept();
			TracedConcept tc = getTracedConceptWith(negatedConcept);
			if (tc != null && m_HomePackageID.equals(context)) {
				addClashCause(m_Suspect, tc);
			}
		}
	
	}

	@Override
	public String toString() {
		return "#" + m_ID;
	}
	
	public String getLabels() {
		return m_Labels.values().toString();
	}

}
