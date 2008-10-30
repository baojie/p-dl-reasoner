package edu.iastate.pdlreasoner.tableau;

import java.util.LinkedList;
import java.util.List;
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
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TableauManager {
	
	private TableauServer m_Server;
	private DLPackage m_Package;
	private TBox m_TBox;
	private List<TracedConcept> m_TracedUC;
	private TableauGraph m_Graph;
	private Clock m_Clock;
	private boolean m_HasToken;
	private Queue<Message> m_ReceivedMsgs;
	
	private ConceptExpander m_ConceptExpander;
	private MessageProcessor m_MessageProcessor;
	
	public TableauManager(KnowledgeBase kb) {
		m_Package = kb.getPackage();
		m_TBox = kb.getTBox();
		m_TracedUC = CollectionUtil.makeList();
		for (Concept uc : m_TBox.getUC()) {
			m_TracedUC.add(TracedConcept.makeOrigin(uc));
		}
		m_Graph = new TableauGraph(m_Package);
		m_Clock = new Clock();
		m_HasToken = false;
		m_ReceivedMsgs = new LinkedList<Message>();
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
		root.addLabel(TracedConcept.makeOrigin(c));
		applyUniversalRestriction(root);
	}
	
	public void synchronizeClockWith(Clock c) {
		m_Clock.copy(c);
	}
	
	public void setToken(boolean v) {
		m_HasToken = v;
	}
	
	public void receive(Message msg) {
		m_ReceivedMsgs.offer(msg);
	}
	
	public void run() {
		processMessages();
		expandGraph();
	}

	private void processMessages() {
		while (!m_ReceivedMsgs.isEmpty()) {
			m_ReceivedMsgs.remove().execute(m_MessageProcessor);
		}		
	}

	private void expandGraph() {
		for (Node open : m_Graph.getOpenNodes()) {
			m_ConceptExpander.reset(open);
			
			for (TracedConcept tc : open.getLabelsFor(And.class).flush()) {
				m_ConceptExpander.expand(tc);
			}
			for (TracedConcept tc : open.getLabelsFor(SomeValues.class).flush()) {
				m_ConceptExpander.expand(tc);
			}
			for (TracedConcept tc : open.getLabelsFor(AllValues.class).flush()) {
				m_ConceptExpander.expand(tc);
			}
			
			if (m_HasToken) {
				for (TracedConcept tc : open.getLabelsFor(Or.class).flush()) {
					m_ConceptExpander.expand(tc);
				}
			}
		}
	}

	private void applyUniversalRestriction(Node n) {
		for (TracedConcept uc : m_TracedUC) {
			n.addLabel(uc);
		}
	}

	private class ConceptExpander extends ConceptVisitorAdapter {
		
		private Node m_Node;
		private TracedConcept m_Concept;
		
		public void reset(Node n) {
			m_Node = n;
		}
		
		public void expand(TracedConcept tc) {
			m_Concept = tc;
			tc.accept(this);
		}
		
		@Override
		public void visit(And and) {
			for (Concept c : and.getOperands()) {
				m_Node.addLabel(m_Concept.derive(c));
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
				Node child = m_Node.addChildWith(role, m_Concept.derive(filler));
				applyUniversalRestriction(child);
				
				for (TracedConcept tc : m_Node.getLabelsFor(AllValues.class).getExpanded()) {
					AllValues all = (AllValues) tc.getConcept();
					if (role.equals(all.getRole())) {
						child.addLabel(tc.derive(all.getFiller()));
					}
				}
			}
		}

		@Override
		public void visit(AllValues allValues) {
			Role role = allValues.getRole();
			Concept filler = allValues.getFiller();
			for (Node child : m_Node.getChildrenWith(role)) {
				child.addLabel(m_Concept.derive(filler));
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
