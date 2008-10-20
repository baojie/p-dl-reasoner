package edu.iastate.pdlreasoner.tableau;

import java.util.Set;

import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TableauGraph {

	private DLPackage m_Package;
	private Set<Node> m_Roots;
	private GraphClashDetector m_ClashDetector;
	private OpenNodesCollector m_OpenNodesCollector;

	public TableauGraph(DLPackage dlPackage) {
		m_Package = dlPackage;
		m_Roots = CollectionUtil.makeSet();
		m_ClashDetector = new GraphClashDetector();
		m_OpenNodesCollector = new OpenNodesCollector();
	}
	
	public DLPackage getPackage() {
		return m_Package;
	}
	
	public Node makeRoot() {
		Node n = Node.make(this);
		m_Roots.add(n);
		return n;
	}

	public boolean hasClash() {
		m_ClashDetector.reset();
		for (Node root : m_Roots) {
			root.accept(m_ClashDetector);
			if (m_ClashDetector.hasClash()) return true;
		}
		return false;
	}
	
	public Set<Node> getOpenNodes() {
		m_OpenNodesCollector.reset();
		for (Node root : m_Roots) {
			root.accept(m_OpenNodesCollector);
		}
		return m_OpenNodesCollector.getNodes();
	}
	
	private static class GraphClashDetector implements NodeVisitor {
		
		private boolean m_HasClash;
		
		public void reset() {
			m_HasClash = false;
		}
		
		public boolean hasClash() {
			return m_HasClash;
		}

		@Override
		public void visit(Node n) {
			if (n.hasClash()) {
				m_HasClash = true;
			}
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
