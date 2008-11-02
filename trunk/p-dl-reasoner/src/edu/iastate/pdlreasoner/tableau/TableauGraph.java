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

	public TableauGraph(DLPackage dlPackage) {
		m_Package = dlPackage;
		m_Roots = CollectionUtil.makeSet();
		m_Branches = CollectionUtil.makeList();
		m_ClashDetector = new ClashCauseCollector();
		m_OpenNodesCollector = new OpenNodesCollector();
	}
	
	public DLPackage getPackage() {
		return m_Package;
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
	
	public void prune(BranchPoint restoreTarget) {
		
	}

	public BranchPoint getEarliestClashCause() {
		m_ClashDetector.reset();
		for (Node root : m_Roots) {
			root.accept(m_ClashDetector);
		}
		Set<BranchPoint> clashCauses = m_ClashDetector.getClashCauses();
		return clashCauses.isEmpty() ? null : Collections.min(clashCauses);
	}
	
	public Set<Node> getOpenNodes() {
		m_OpenNodesCollector.reset();
		for (Node root : m_Roots) {
			root.accept(m_OpenNodesCollector);
		}
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

}
