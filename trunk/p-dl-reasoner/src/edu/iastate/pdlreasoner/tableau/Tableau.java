package edu.iastate.pdlreasoner.tableau;

import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

import edu.iastate.pdlreasoner.kb.ImportGraph;
import edu.iastate.pdlreasoner.kb.OntologyPackage;
import edu.iastate.pdlreasoner.kb.Query;
import edu.iastate.pdlreasoner.kb.TBox;
import edu.iastate.pdlreasoner.master.graph.GlobalNodeID;
import edu.iastate.pdlreasoner.message.BackwardConceptReport;
import edu.iastate.pdlreasoner.message.Clash;
import edu.iastate.pdlreasoner.message.ForwardConceptReport;
import edu.iastate.pdlreasoner.message.MakeGlobalRoot;
import edu.iastate.pdlreasoner.message.MakePreImage;
import edu.iastate.pdlreasoner.message.MessageToSlave;
import edu.iastate.pdlreasoner.message.Null;
import edu.iastate.pdlreasoner.message.ReopenAtoms;
import edu.iastate.pdlreasoner.message.TableauSlaveMessageProcessor;
import edu.iastate.pdlreasoner.model.AllValues;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.ContextualizedConcept;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.model.Role;
import edu.iastate.pdlreasoner.model.SomeValues;
import edu.iastate.pdlreasoner.model.Top;
import edu.iastate.pdlreasoner.model.visitor.ConceptVisitorAdapter;
import edu.iastate.pdlreasoner.net.ChannelUtil;
import edu.iastate.pdlreasoner.tableau.branch.Branch;
import edu.iastate.pdlreasoner.tableau.branch.BranchPoint;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;
import edu.iastate.pdlreasoner.tableau.branch.BranchToken;
import edu.iastate.pdlreasoner.tableau.graph.Edge;
import edu.iastate.pdlreasoner.tableau.graph.Node;
import edu.iastate.pdlreasoner.tableau.graph.TableauGraph;

public class Tableau {
	
	private static final Logger LOGGER = Logger.getLogger(Tableau.class);
	
	private static enum State { ENTRY, READY, EXPAND, EXIT }
	
	//Constants once set
	private Query m_Query;
	private Address m_MasterAdd;
	private PackageID m_AssignedPackageID;
	private OntologyPackage m_AssignedPackage;
	private ImportGraph m_ImportGraph;
	private TBox m_TBox;
	
	//Variables
	private Channel m_Channel;
	private BlockingQueue<Message> m_MessageQueue;
	private State m_State;
	private TableauGraph m_Graph;
	private BranchToken m_Token;
	
	//Processors
	private ConceptExpander m_ConceptExpander;
	private TableauSlaveMessageProcessor m_MessageProcessor;
	
	public Tableau() {
		m_MessageQueue = new LinkedBlockingQueue<Message>();
		m_State = State.ENTRY;
	}
	
	public void run(Query query) throws ChannelException, InterruptedException {
		m_Query = query;
		initChannel();
		
		while (m_State != State.EXIT) {
			Message msg = null;
			switch (m_State) {
			case ENTRY:
				msg = m_MessageQueue.take();
				m_MasterAdd = msg.getSrc();
				m_AssignedPackageID = (PackageID) msg.getObject();
				initTableau();
				m_State = State.READY;
				break;
				
			case READY:
				processOneTableauMessage();
				m_State = State.EXPAND;
				break;
				
			case EXPAND:
				while (!m_MessageQueue.isEmpty()) {
					processOneTableauMessage();
				}
				
				expandGraph();
				processClash();
				
				if (m_Token != null) {
					releaseToken();
				}
				break;
			}
		}
		
		m_Channel.close();
	}

	private void initChannel() throws ChannelException {
		m_Channel = new JChannel();
		m_Channel.connect(ChannelUtil.getSessionName());
		m_Channel.setReceiver(new ReceiverAdapter() {
				public void receive(Message msg) {
					while (true) {
						try {
							m_MessageQueue.put(msg);
						} catch (InterruptedException e) {
							e.printStackTrace();
							continue;
						}
					}
				}
			});
	}

	private void initTableau() {
		m_ImportGraph = m_Query.getOntology().getImportGraph();
		
		for (OntologyPackage pack : m_Query.getOntology().getPackages()) {
			if (pack.getID().equals(m_AssignedPackageID)) {
				m_AssignedPackage = pack;
				break;
			}
		}
		
		m_TBox = m_AssignedPackage.getTBox();
		m_Graph = new TableauGraph(m_AssignedPackageID);
		m_Token = null;
		m_ConceptExpander = new ConceptExpander();
		m_MessageProcessor = new TableauMessageProcessorImpl();
	}
	
	private void processOneTableauMessage() throws InterruptedException {
		Message msg = m_MessageQueue.take();
		MessageToSlave tabMsg = (MessageToSlave) msg.getObject();
		tabMsg.execute(m_MessageProcessor);
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
				LOGGER.debug(m_AssignedPackageID.toDebugString() + "applied deterministic rules on node " + open + ": " + open.getLabels());
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
			LOGGER.debug(m_AssignedPackageID.toDebugString() + "broadcasting clash " + clashCause);
		}
		
		m_Master.processClash(clashCause);
	}

	private void releaseToken() {
		BranchToken temp = m_Token;
		m_Token = null;
		m_Master.returnTokenFrom(this, temp);
	}

	
	private void applyUniversalRestriction(Node n) {
		BranchPointSet nodeDependency = n.getDependency();
		for (Concept uc : m_TBox.getUC()) {
			n.addLabel(new TracedConcept(uc, nodeDependency));
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(m_AssignedPackageID.toDebugString() + "applied UR on node " + n + ": " + n.getLabels());
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
			PackageID context = c.getContext();
			if (!m_AssignedPackageID.equals(context)) {
				GlobalNodeID importSource = GlobalNodeID.makeWithUnknownID(context);
				GlobalNodeID importTarget = m_Node.getGlobalNodeID();
				BackwardConceptReport backward = new BackwardConceptReport(importSource, importTarget, m_Concept);
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(m_AssignedPackageID.toDebugString() + "sending " + backward);
				}
				
				m_InterTableauMan.processConceptReport(backward);
			} else {
				List<PackageID> importers = m_ImportGraph.getImportersOf(m_AssignedPackageID, c);
				if (importers != null) {
					GlobalNodeID importSource = m_Node.getGlobalNodeID();
					for (PackageID importer : importers) {
						GlobalNodeID importTarget = GlobalNodeID.makeWithUnknownID(importer);
						ForwardConceptReport forward = new ForwardConceptReport(importSource, importTarget, m_Concept);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(m_AssignedPackageID.toDebugString() + "sending " + forward);
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
	
	private class TableauMessageProcessorImpl implements TableauSlaveMessageProcessor {
		
		@Override
		public void process(Clash msg) {
			BranchPointSet clashCause = msg.getCause();
			if (clashCause.isEmpty()) {
				m_State = State.EXIT;
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
				LOGGER.debug(m_AssignedPackageID.toDebugString() + "applied forward concept report on node " + node + ": " + node.getLabels());
			}
		}

		@Override
		public void process(BackwardConceptReport msg) {
			Node node = m_Graph.get(msg.getImportSource());
			TracedConcept concept = msg.getConcept();
			BranchPointSet unionDepends = BranchPointSet.union(node.getDependency(), concept.getDependency());
			node.addLabel(new TracedConcept(concept.getConcept(), unionDepends));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(m_AssignedPackageID.toDebugString() + "applied backward concept report on node " + node + ": " + node.getLabels());
			}
		}

		@Override
		public void process(MakePreImage msg) {
			Node preImage = m_Graph.makeNode(msg.getGlobalNodeID(), msg.getDependency());
			m_Graph.addRoot(preImage);
			applyUniversalRestriction(preImage);
		}

		@Override
		public void process(ReopenAtoms msg) {
			m_Graph.reopenAtomsOnGlobalNodes(msg.getNodes());
		}

		@Override
		public void process(MakeGlobalRoot msg) {
			Node root = m_Graph.makeNode(BranchPointSet.EMPTY);
			m_Graph.addRoot(root);
			root.addLabel(TracedConcept.makeOrigin(msg.getConcept()));
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(m_AssignedPackageID.toDebugString() + "starting with global root " + root + ": " + root.getLabels());
			}
			
			applyUniversalRestriction(root);
			
			m_Token = BranchToken.make();
		}

		@Override
		public void process(Null msg) {
		}
		
	}

}
