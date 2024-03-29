package edu.iastate.pdlreasoner.tableau;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.iastate.pdlreasoner.kb.KnowledgeBase;
import edu.iastate.pdlreasoner.kb.TBox;
import edu.iastate.pdlreasoner.model.AllValues;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.ContextualizedConcept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.Role;
import edu.iastate.pdlreasoner.model.SomeValues;
import edu.iastate.pdlreasoner.model.Top;
import edu.iastate.pdlreasoner.model.visitor.ConceptVisitorAdapter;
import edu.iastate.pdlreasoner.server.ImportGraph;
import edu.iastate.pdlreasoner.server.InterTableauManager;
import edu.iastate.pdlreasoner.server.TableauServer;
import edu.iastate.pdlreasoner.server.graph.GlobalNodeID;
import edu.iastate.pdlreasoner.tableau.branch.Branch;
import edu.iastate.pdlreasoner.tableau.branch.BranchPoint;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;
import edu.iastate.pdlreasoner.tableau.branch.BranchToken;
import edu.iastate.pdlreasoner.tableau.graph.Edge;
import edu.iastate.pdlreasoner.tableau.graph.Node;
import edu.iastate.pdlreasoner.tableau.graph.TableauGraph;
import edu.iastate.pdlreasoner.tableau.message.BackwardConceptReport;
import edu.iastate.pdlreasoner.tableau.message.Clash;
import edu.iastate.pdlreasoner.tableau.message.ForwardConceptReport;
import edu.iastate.pdlreasoner.tableau.message.Message;
import edu.iastate.pdlreasoner.tableau.message.MessageProcessor;

public class TableauManager {
	
	private static final Logger LOGGER = Logger.getLogger(TableauManager.class);
	
	//Constants
	private TableauServer m_Server;
	private ImportGraph m_ImportGraph;
	private InterTableauManager m_InterTableauMan;
	private DLPackage m_Package;
	private TBox m_TBox;
	
	//Variables
	private TableauGraph m_Graph;
	private BranchToken m_Token;
	private Queue<Message> m_ReceivedMsgs;
	private boolean m_HasClashAtOrigin;
	
	//Processors
	private ConceptExpander m_ConceptExpander;
	private MessageProcessor m_MessageProcessor;
	
	public TableauManager(KnowledgeBase kb) {
		m_Package = kb.getPackage();
		m_TBox = kb.getTBox();
		m_Graph = new TableauGraph(m_Package);
		m_Token = null;
		m_ReceivedMsgs = new LinkedList<Message>();
		m_HasClashAtOrigin = false;
		m_ConceptExpander = new ConceptExpander();
		m_MessageProcessor = new MessageProcessorImpl();
	}
	
	public DLPackage getPackage() {
		return m_Package;
	}
	
	public void setServer(TableauServer server) {
		m_Server = server;
	}

	public void setImportGraph(ImportGraph importGraph) {
		m_ImportGraph = importGraph;
	}

	public void setInterTableauManager(InterTableauManager interTableauMan) {
		m_InterTableauMan = interTableauMan;
	}
	
	public void addGlobalRootWith(Concept c) {
		Node root = m_Graph.makeRoot(BranchPointSet.EMPTY);
		root.addLabel(TracedConcept.makeOrigin(c));
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(m_Package.toDebugString() + "starting with global root " + root + ": " + root.getLabels());
		}
		
		applyUniversalRestriction(root);
	}
	
	public GlobalNodeID addRoot(BranchPointSet dependency) {
		Node root = m_Graph.makeRoot(dependency);
		applyUniversalRestriction(root);
		return root.getGlobalNodeID();
	}
	
	public boolean isComplete() {
		return m_ReceivedMsgs.isEmpty() && 
			(m_HasClashAtOrigin || m_Graph.getOpenNodes().isEmpty());
	}
	
	public boolean hasPendingMessages() {
		return !m_ReceivedMsgs.isEmpty();
	}
	
	public boolean hasClashAtOrigin() {
		return m_HasClashAtOrigin;
	}

	public void receiveToken(BranchToken tok) {
		m_Token = tok;
	}
	
	public void receive(Message msg) {
		m_ReceivedMsgs.offer(msg);
	}
	
	public boolean isOwnerOf(BranchPoint restoreTarget) {
		return m_Graph.hasBranch(restoreTarget);
	}
	
	public void tryNextChoiceOnClashedBranchWith(BranchPointSet clashCause) {
		Branch branch = m_Graph.getLastBranch();
		branch.setLastClashCause(clashCause);
		branch.tryNext();
	}
	
	public void reopenAtomsOnGlobalNodes(Set<GlobalNodeID> nodes) {
		m_Graph.reopenAtomsOnGlobalNodes(nodes);
	}

	public void run() {
		processMessages();
		if (m_HasClashAtOrigin) return;
		if (m_Server.isSynchronizingForClash()) {
			processClash();
			return;
		}
		
		expandGraph();
		processClash();
		
		if (m_Token != null) {
			releaseToken();
		}
	}

	private void processMessages() {
		while (!m_ReceivedMsgs.isEmpty()) {
			m_ReceivedMsgs.remove().execute(m_MessageProcessor);
		}		
	}
	
	private void expandGraph() {
		for (Node open : m_Graph.getOpenNodes()) {
			m_ConceptExpander.reset(open);
			
			boolean hasChanged = false;
			hasChanged = hasChanged | expand(open.getLabelsFor(Bottom.class));
			hasChanged = hasChanged | expand(open.getLabelsFor(Top.class));
			hasChanged = hasChanged | expand(open.getLabelsFor(Atom.class));
			hasChanged = hasChanged | expand(open.getLabelsFor(Negation.class));
			hasChanged = hasChanged | expand(open.getLabelsFor(And.class));
			hasChanged = hasChanged | expand(open.getLabelsFor(SomeValues.class));
			hasChanged = hasChanged | expand(open.getLabelsFor(AllValues.class));
			
			if (LOGGER.isDebugEnabled() && hasChanged) {
				LOGGER.debug(m_Package.toDebugString() + "applied deterministic rules on node " + open + ": " + open.getLabels());
			}
			
			if (m_Token != null) {
				expand(open.getLabelsFor(Or.class));
			}
		}
	}
	
	private boolean expand(TracedConceptSet tcSet) {
		if (tcSet == null) return false;
			
		Set<TracedConcept> tcs = tcSet.flush();
		for (TracedConcept tc : tcs) {
			m_ConceptExpander.expand(tc);
		}
		
		return !tcs.isEmpty();
	}
	
	private void processClash() {
		BranchPointSet clashCause = m_Graph.getEarliestClashCause();
		if (clashCause == null) return;
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(m_Package.toDebugString() + "broadcasting clash " + clashCause);
		}
		
		m_Server.processClash(clashCause);
	}

	private void releaseToken() {
		BranchToken temp = m_Token;
		m_Token = null;
		m_Server.returnTokenFrom(this, temp);
	}

	private void applyUniversalRestriction(Node n) {
		BranchPointSet nodeDependency = n.getDependency();
		for (Concept uc : m_TBox.getUC()) {
			n.addLabel(new TracedConcept(uc, nodeDependency));
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(m_Package.toDebugString() + "applied UR on node " + n + ": " + n.getLabels());
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

		private void visitAtomOrTop(ContextualizedConcept c) {
			DLPackage context = c.getContext();
			if (!m_Package.equals(context)) {
				GlobalNodeID importSource = GlobalNodeID.makeWithUnknownID(context);
				GlobalNodeID importTarget = m_Node.getGlobalNodeID();
				BackwardConceptReport backward = new BackwardConceptReport(importSource, importTarget, m_Concept);
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(m_Package.toDebugString() + "sending " + backward);
				}
				
				m_InterTableauMan.processConceptReport(backward);
			} else {
				List<DLPackage> importers = m_ImportGraph.getImportersOf(m_Package, c);
				if (importers != null) {
					GlobalNodeID importSource = m_Node.getGlobalNodeID();
					for (DLPackage importer : importers) {
						GlobalNodeID importTarget = GlobalNodeID.makeWithUnknownID(importer);
						ForwardConceptReport forward = new ForwardConceptReport(importSource, importTarget, m_Concept);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(m_Package.toDebugString() + "sending " + forward);
						}

						m_InterTableauMan.processConceptReport(forward);
					}
				}
			}
		}

		@Override
		public void visit(Top top) {
			visitAtomOrTop(top);
		}

		@Override
		public void visit(Atom atom) {
			visitAtomOrTop(atom);
		}

		@Override
		public void visit(And and) {
			for (Concept c : and.getOperands()) {
				m_Node.addLabel(m_Concept.derive(c));
			}
		}

		@Override
		public void visit(Or or) {
			Branch branch = new Branch(m_Node, m_Concept, m_Token.makeNextBranchPoint());
			m_Graph.addBranch(branch);

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
				
				TracedConceptSet allValuesSet = m_Node.getLabelsFor(AllValues.class);
				if (allValuesSet != null) {
					for (TracedConcept tc : allValuesSet.getExpanded()) {
						AllValues all = (AllValues) tc.getConcept();
						if (role.equals(all.getRole())) {
							BranchPointSet unionDepends = BranchPointSet.union(m_Node.getDependency(), tc.getDependency());
							child.addLabel(new TracedConcept(all.getFiller(), unionDepends));
						}
					}
				}
			}
		}

		@Override
		public void visit(AllValues allValues) {
			Role role = allValues.getRole();
			Concept filler = allValues.getFiller();
			for (Edge edge : m_Node.getChildrenWith(role)) {
				Node child = edge.getChild();
				BranchPointSet unionDepends = BranchPointSet.union(child.getDependency(), m_Concept.getDependency());
				child.addLabel(new TracedConcept(filler, unionDepends));
			}
		}

	}
	
	private class MessageProcessorImpl implements MessageProcessor {
		
		@Override
		public void process(Clash msg) {
			BranchPointSet clashCause = msg.getCause();
			if (clashCause.isEmpty()) {
				m_HasClashAtOrigin = true;
			} else {
				BranchPoint restoreTarget = clashCause.getLatestBranchPoint();
				m_Graph.pruneTo(restoreTarget);
			}
		}

		@Override
		public void process(ForwardConceptReport msg) {
			Node node = m_Graph.get(msg.getImportTarget());
			node.addLabel(msg.getConcept());
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(m_Package.toDebugString() + "applied forward concept report on node " + node + ": " + node.getLabels());
			}
		}

		@Override
		public void process(BackwardConceptReport msg) {
			Node node = m_Graph.get(msg.getImportSource());
			TracedConcept concept = msg.getConcept();
			BranchPointSet unionDepends = BranchPointSet.union(node.getDependency(), concept.getDependency());
			node.addLabel(new TracedConcept(concept.getConcept(), unionDepends));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(m_Package.toDebugString() + "applied backward concept report on node " + node + ": " + node.getLabels());
			}
		}
		
	}

}
