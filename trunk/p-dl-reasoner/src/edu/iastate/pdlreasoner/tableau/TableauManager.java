package edu.iastate.pdlreasoner.tableau;

import java.util.LinkedList;
import java.util.Queue;

import edu.iastate.pdlreasoner.kb.KnowledgeBase;
import edu.iastate.pdlreasoner.kb.TBox;
import edu.iastate.pdlreasoner.model.AllValues;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.Role;
import edu.iastate.pdlreasoner.model.SomeValues;
import edu.iastate.pdlreasoner.model.Top;
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
	private Queue<Message> m_ReceivedMsgs;
	private boolean m_HasClashAtOrigin;
	
	private ConceptExpander m_ConceptExpander;
	private MessageProcessor m_MessageProcessor;
	
	public TableauManager(KnowledgeBase kb) {
		m_Package = kb.getPackage();
		m_TBox = kb.getTBox();
		m_Graph = new TableauGraph(m_Package);
		m_Clock = new Clock();
		m_HasToken = false;
		m_ReceivedMsgs = new LinkedList<Message>();
		m_HasClashAtOrigin = false;
		m_ConceptExpander = new ConceptExpander();
		m_MessageProcessor = new MessageProcessorImpl();
	}
	
	public void setServer(TableauServer server) {
		m_Server = server;
	}

	public boolean isComplete() {
		return m_HasClashAtOrigin || m_Graph.getOpenNodes().isEmpty();
	}
	
	public boolean hasClashAtOrigin() {
		return m_HasClashAtOrigin;
	}

	public void addRootWith(Concept c) {
		Node root = m_Graph.makeRoot(BranchPoint.ORIGIN);
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
		processClash();
	}

	private void processMessages() {
		while (!m_ReceivedMsgs.isEmpty()) {
			m_ReceivedMsgs.remove().execute(m_MessageProcessor);
		}		
	}
	
	private void tryNextChoiceOn(BranchPoint branchPoint) {
		
	}

	private void expandGraph() {
		for (Node open : m_Graph.getOpenNodes()) {
			m_ConceptExpander.reset(open);
			
			expand(open.getLabelsFor(Bottom.class));
			expand(open.getLabelsFor(Top.class));
			expand(open.getLabelsFor(Atom.class));
			expand(open.getLabelsFor(Negation.class));
			expand(open.getLabelsFor(And.class));
			expand(open.getLabelsFor(SomeValues.class));
			expand(open.getLabelsFor(AllValues.class));
			if (m_HasToken) {
				expand(open.getLabelsFor(Or.class));
			}
		}
	}
	
	private void expand(TracedConceptSet tcSet) {
		if (tcSet != null) {
			for (TracedConcept tc : tcSet.flush()) {
				m_ConceptExpander.expand(tc);
			}
		}
	}
	
	private void processClash() {
		BranchPoint clashCause = m_Graph.getEarliestClashCause();
		if (clashCause == null) {
			return;
		} else if (clashCause == BranchPoint.ORIGIN) {
			m_HasClashAtOrigin = true;
		}
		
		broadcastClash(clashCause);
	}

	private void broadcastClash(BranchPoint clashCause) {
		m_Server.broadcast(new Clash(clashCause));
	}

	private void applyUniversalRestriction(Node n) {
		BranchPoint nodeDependency = n.getDependency();
		for (Concept uc : m_TBox.getUC()) {
			n.addLabel(new TracedConcept(uc, nodeDependency));
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
			Branch branch = new Branch(m_Node, m_Concept);
			m_Graph.addBranch(branch, m_Clock.getTime());
			
			for (Concept disjunct : or.getOperands()) {
				if (m_Node.containsLabel(disjunct)) return;
			}
			
			branch.tryNext();
		}

		@Override
		public void visit(SomeValues someValues) {
			Role role = someValues.getRole();
			Concept filler = someValues.getFiller();
			if (!m_Node.containsChild(role, filler)) {
				Node child = m_Node.addChildBy(role, m_Concept.getDependency());
				child.addLabel(m_Concept.derive(filler));
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
			BranchPoint restoreTarget = msg.getRestoreTarget();
			m_Graph.prune(restoreTarget);
			if (m_Package.equals(restoreTarget.getPackage())) {
				m_Clock.setTime(restoreTarget.getTime());
				tryNextChoiceOn(restoreTarget);
			}
		}

		@Override
		public void process(CPush msg) {
		}

		@Override
		public void process(CReport msg) {
		}
		
	}
	
}
