package edu.iastate.pdlreasoner.tableau;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TableauGraph {

	private DLPackage m_Package;
	private Set<Node> m_Roots;
	private List<Branch> m_Branches;
	
	private ClashCauseCollector m_ClashDetector;
	private OpenNodesCollector m_OpenNodesCollector;
	private PruneNodesCollector m_PruneNodesCollector;
	private ConceptPruner m_ConceptPruner;

	public TableauGraph(DLPackage dlPackage) {
		m_Package = dlPackage;
		m_Roots = CollectionUtil.makeSet();
		m_Branches = CollectionUtil.makeList();
		m_ClashDetector = new ClashCauseCollector();
		m_OpenNodesCollector = new OpenNodesCollector();
		m_PruneNodesCollector = new PruneNodesCollector();
		m_ConceptPruner = new ConceptPruner();
	}
	
	public DLPackage getPackage() {
		return m_Package;
	}
	
	public void accept(NodeVisitor v) {
		for (Node root : m_Roots) {
			root.accept(v);
		}
	}
	
	public Node makeRoot(BranchPoint dependency) {
		Node n = Node.make(this, dependency);
		m_Roots.add(n);
		return n;
	}

	public void addBranch(Branch branch, int time) {
		branch.setBranchPoint(new BranchPoint(time, m_Package, m_Branches.size()));
		m_Branches.add(branch);
	}
	
	public Branch getBranch(int index) {
		return m_Branches.get(index);
	}
	
	public void pruneTo(BranchPoint restoreTarget) {
		//Prune nodes
		m_PruneNodesCollector.reset(restoreTarget);
		accept(m_PruneNodesCollector);
		for (Node n : m_PruneNodesCollector.getNodes()) {
			if (!m_Roots.remove(n)) {
				n.removeFromParent();
			}
		}
		
		//Prune and reopen concepts on remaining node
		m_ConceptPruner.reset(restoreTarget);
		accept(m_ConceptPruner);
		
		//Prune branches
		int targetIndex = restoreTarget.getBranchIndex();
		for (int i = m_Branches.size() - 1; i > targetIndex; i--) {
			Branch iBranch = m_Branches.get(i);
			if (restoreTarget.beforeOrEquals(iBranch.getDependency())) {
				m_Branches.remove(i);
			}
		}
		
		//Reopen remaining branches - those that do not depend on the restoreTarget
		//but still have to be pruned to make sure that restoreTarget is the latest branch.
		for (int i = m_Branches.size() - 1; i > targetIndex; i--) {
			Branch iBranch = m_Branches.remove(i);
			iBranch.reopenConceptOnNode();
		}
	}

	public BranchPoint getEarliestClashCause() {
		m_ClashDetector.reset();
		accept(m_ClashDetector);
		Set<BranchPoint> clashCauses = m_ClashDetector.getClashCauses();
		return clashCauses.isEmpty() ? null : Collections.min(clashCauses);
	}
	
	public Set<Node> getOpenNodes() {
		m_OpenNodesCollector.reset();
		accept(m_OpenNodesCollector);
		return m_OpenNodesCollector.getNodes();
	}
	
	private static class ClashCauseCollector implements NodeVisitor {
		
		private Set<BranchPoint> m_ClashCauses;
		
		public ClashCauseCollector() {
			m_ClashCauses = CollectionUtil.makeSet();
		}
		
		public void reset() {
			m_ClashCauses.clear();
		}
		
		public Set<BranchPoint> getClashCauses() {
			return m_ClashCauses;
		}

		@Override
		public void visit(Node n) {
			m_ClashCauses.addAll(n.getClashCauses());
			n.clearClashCauses();
		}
		
	}

	private static class OpenNodesCollector implements NodeVisitor {
		
		private Set<Node> m_Nodes;
		
		public OpenNodesCollector() {
			m_Nodes = CollectionUtil.makeSet();
		}
		
		public void reset() {
			m_Nodes.clear();
		}
		
		public Set<Node> getNodes() {
			return m_Nodes;
		}

		@Override
		public void visit(Node n) {
			if (!n.isComplete()) {
				m_Nodes.add(n);
			}
		}
		
	}

	private static class PruneNodesCollector implements NodeVisitor {
		
		private Set<Node> m_Nodes;
		private BranchPoint m_RestoreTarget;
		
		public PruneNodesCollector() {
			m_Nodes = CollectionUtil.makeSet();
		}
		
		public void reset(BranchPoint restoreTarget) {
			m_Nodes.clear();
			m_RestoreTarget = restoreTarget;
		}
		
		public Set<Node> getNodes() {
			return m_Nodes;
		}

		@Override
		public void visit(Node n) {
			if (m_RestoreTarget.beforeOrEquals(n.getDependency())) {
				m_Nodes.add(n);
			}
		}
		
	}

	private static class ConceptPruner implements NodeVisitor {
		
		private BranchPoint m_RestoreTarget;
		
		public void reset(BranchPoint restoreTarget) {
			m_RestoreTarget = restoreTarget;
		}
		
		@Override
		public void visit(Node n) {
			n.pruneAndReopenLabels(m_RestoreTarget);
		}
		
	}

}
