package edu.iastate.pdlreasoner.tableau;

import edu.iastate.pdlreasoner.kb.KnowledgeBase;
import edu.iastate.pdlreasoner.kb.TBox;
import edu.iastate.pdlreasoner.model.AllValues;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.Role;
import edu.iastate.pdlreasoner.model.SomeValues;
import edu.iastate.pdlreasoner.model.visitor.ConceptVisitorAdapter;
import edu.iastate.pdlreasoner.server.TableauServer;

public class TableauManager {
	
	private TableauServer m_Server;
	private DLPackage m_Package;
	private TBox m_TBox;
	private TableauGraph m_Graph;
	private ConceptExpander m_ConceptExpander;
	
	public TableauManager(KnowledgeBase kb) {
		m_Package = kb.getPackage();
		m_TBox = kb.getTBox();
		m_Graph = new TableauGraph(m_Package);
		m_ConceptExpander = new ConceptExpander();
	}
	
	public void setServer(TableauServer server) {
		m_Server = server;
	}

	public boolean isComplete() {
		return m_Graph.getOpenNodes().isEmpty();
	}
	
	public boolean hasClash() {
		return m_Graph.hasClash();
	}

	public void addRootWith(Concept c) {
		Node root = m_Graph.makeRoot();
		root.addLabel(c);
		applyUniversalRestriction(root);
	}
	
	public void expand() {
		for (Node open : m_Graph.getOpenNodes()) {
			m_ConceptExpander.reset(open);
			for (Concept c : open.flushOpenLabels()) {
				c.accept(m_ConceptExpander);
			}
		}
	}

	private void applyUniversalRestriction(Node n) {
		for (Concept uc : m_TBox.getUC()) {
			n.addLabel(uc);
		}
	}

	private class ConceptExpander extends ConceptVisitorAdapter {
		
		private Node m_Node;
		
		public void reset(Node n) {
			m_Node = n;
		}
		
		@Override
		public void visit(And and) {
			for (Concept c : and.getOperands()) {
				m_Node.addLabel(c);
			}
		}

		@Override
		public void visit(Or or) {
		}

		@Override
		public void visit(SomeValues someValues) {
			Role role = someValues.getRole();
			Concept filler = someValues.getFiller();
			if (!m_Node.containsChild(role, filler)) {
				Node child = m_Node.addChildWith(role, filler);
				applyUniversalRestriction(child);
				
				for (AllValues all : m_Node.getExpandedAllValuesWith(role)) {
					Concept allValuesFiller = all.getFiller();
					child.addLabel(allValuesFiller);
				}
			}
		}

		@Override
		public void visit(AllValues allValues) {
			Role role = allValues.getRole();
			Concept filler = allValues.getFiller();
			for (Node child : m_Node.getChildrenWith(role)) {
				child.addLabel(filler);
			}
		}

	}
	
}
