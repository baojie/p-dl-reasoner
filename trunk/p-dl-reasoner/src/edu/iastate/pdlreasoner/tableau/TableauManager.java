package edu.iastate.pdlreasoner.tableau;

import java.util.LinkedList;
import java.util.Queue;

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
import edu.iastate.pdlreasoner.tableau.messaging.CPush;
import edu.iastate.pdlreasoner.tableau.messaging.CReport;
import edu.iastate.pdlreasoner.tableau.messaging.Clash;
import edu.iastate.pdlreasoner.tableau.messaging.Message;
import edu.iastate.pdlreasoner.tableau.messaging.MessageProcessor;

public class TableauManager {
	
	private TableauServer m_Server;
	private DLPackage m_Package;
	private TBox m_TBox;
	private TableauGraph m_Graph;
	private Clock m_Clock;
	private boolean m_HasToken;
	private Queue<Message> m_MessageQueue;
	
	private ConceptExpander m_ConceptExpander;
	private MessageProcessor m_MessageProcessor;
	
	public TableauManager(KnowledgeBase kb) {
		m_Package = kb.getPackage();
		m_TBox = kb.getTBox();
		m_Graph = new TableauGraph(m_Package);
		m_Clock = new Clock();
		m_HasToken = false;
		m_MessageQueue = new LinkedList<Message>();
		m_ConceptExpander = new ConceptExpander();
		m_MessageProcessor = new MessageProcessorImpl();
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
	
	public void synchronizeClockWith(Clock c) {
		m_Clock.copy(c);
	}
	
	public void setToken(boolean v) {
		m_HasToken = v;
	}
	
	public void receive(Message msg) {
		m_MessageQueue.offer(msg);
	}
	
	public void run() {
		processMessages();
		expandGraph();
	}

	private void processMessages() {
		while (!m_MessageQueue.isEmpty()) {
			m_MessageQueue.remove().execute(m_MessageProcessor);
		}		
	}

	private void expandGraph() {
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
	
	private class MessageProcessorImpl implements MessageProcessor {
		
		@Override
		public void process(Clash msg) {
			
		}

		@Override
		public void process(CPush msg) {
		}

		@Override
		public void process(CReport msg) {
		}
		
	}
	
}
