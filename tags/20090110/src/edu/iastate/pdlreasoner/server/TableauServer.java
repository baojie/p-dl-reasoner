package edu.iastate.pdlreasoner.server;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.iastate.pdlreasoner.exception.IllegalQueryException;
import edu.iastate.pdlreasoner.kb.KnowledgeBase;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.ModelFactory;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Top;
import edu.iastate.pdlreasoner.model.visitor.ExternalConceptsExtractor;
import edu.iastate.pdlreasoner.tableau.TableauManager;
import edu.iastate.pdlreasoner.tableau.branch.BranchPoint;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;
import edu.iastate.pdlreasoner.tableau.branch.BranchToken;
import edu.iastate.pdlreasoner.tableau.message.Clash;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TableauServer {
	
	private static final Logger LOGGER = Logger.getLogger(TableauServer.class);
	
	//Constants wrt KBs
	private List<KnowledgeBase> m_KBs;
	private ImportGraph m_ImportGraph;
	
	//Variables (new per query)
	private boolean m_HasInitialized;
	private TableauTopology m_Tableaux;
	private InterTableauManager m_InterTableauMan;
	private Set<BranchPointSet> m_ClashCauses;
	
	public TableauServer() {
		m_KBs = CollectionUtil.makeList();
	}
	
	
	//Upper Interfaces
	
	public void addKnowledgeBase(KnowledgeBase kb) {
		m_KBs.add(kb);
	}
	
	public void init() {
		for (KnowledgeBase kb : m_KBs) {
			kb.init();
		}
		
		m_ImportGraph = new ImportGraph(m_KBs);
		m_HasInitialized = true;
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Import graph = " + m_ImportGraph);
			LOGGER.debug("");
		}
	}

	public boolean isUnderstandableBy(Concept c, DLPackage witness) {
		if (!m_HasInitialized) throw new IllegalStateException("TableauServer has not been initialized.");
		
		ExternalConceptsExtractor visitor = new ExternalConceptsExtractor(witness);
		c.accept(visitor);
		
		Set<DLPackage> externals = CollectionUtil.makeSet();
		externals.addAll(visitor.getExternalConcepts().keySet());
		externals.addAll(visitor.getExternalNegationContexts());
		for (DLPackage external : externals) {
			if (!m_ImportGraph.containsEdge(external, witness)) return false;
		}
		
		return true;
	}

	public boolean isSatisfiable(Concept c, DLPackage witness) {
		if (!isUnderstandableBy(c, witness)) {
			throw new IllegalQueryException("Concept " + c + " is not understandable by " + witness);
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Query = satisfiability of " + c + " as witnessed by " + witness);
		}
		
		makeTableaux();
		TableauManager witTableau = m_Tableaux.get(witness);
		witTableau.addGlobalRootWith(c);
		witTableau.receiveToken(BranchToken.make());
		completeAll();
		boolean hasClashAtOrigin = hasClashAtOrigin();
		if (LOGGER.isDebugEnabled()) {
			if (hasClashAtOrigin) {
				LOGGER.debug("All branches clashed");
				LOGGER.debug("Concept " + c + " is not satisfiable as witnessed by " + witness);
			} else {
				LOGGER.debug("Found clash-free completed tableaux");
				LOGGER.debug("Concept " + c + " is satisfiable as witnessed by " + witness);
			}
			LOGGER.debug("");
		}
		
		return !hasClashAtOrigin;
	}
	
	public boolean isConsistent(DLPackage witness) {
		Top topW = ModelFactory.makeTop(witness);
		return isSatisfiable(topW, witness);
	}
	
	public boolean isSubclassOf(Concept sub, Concept sup, DLPackage witness) {
		Negation notSup = ModelFactory.makeNegation(witness, sup);
		And sat = ModelFactory.makeAnd(sub, notSup);
		return !isSatisfiable(sat, witness);
	}
	
	
	//Lower Interfaces
	
	public void processClash(BranchPointSet clashCause) {
		if (m_ClashCauses.add(clashCause)) {
			Clash clash = new Clash(clashCause);
			for (TableauManager tab : m_Tableaux) {
				tab.receive(clash);
			}
		}
	}

	public boolean isSynchronizingForClash() {
		return !m_ClashCauses.isEmpty();
	}
	
	public void returnTokenFrom(TableauManager tab, BranchToken token) {
		if (isSynchronizingForClash()) return;
		
		TableauManager next = m_Tableaux.getNext(tab);
		next.receiveToken(token);
	}
	
	
	//Private

	private void makeTableaux() {
		m_Tableaux = new TableauTopology(m_KBs);
		m_InterTableauMan = new InterTableauManager(m_ImportGraph, m_Tableaux);
		for (TableauManager t : m_Tableaux) {
			t.setServer(this);
			t.setImportGraph(m_ImportGraph);
			t.setInterTableauManager(m_InterTableauMan);
		}
		
		m_ClashCauses = CollectionUtil.makeSet();
	}

	private boolean hasClashAtOrigin() {
		for (TableauManager t : m_Tableaux) {
			if (t.hasClashAtOrigin()) return true;
		}
		return false;
	}
	
	private boolean hasPendingMessages() {
		for (TableauManager t : m_Tableaux) {
			if (t.hasPendingMessages()) return true;
		}
		return false;
	}
	
	private TableauManager findOwnerOf(BranchPointSet clashCause) {
		BranchPoint restoreTarget = clashCause.getLatestBranchPoint();
		for (TableauManager t : m_Tableaux) {
			if (t.isOwnerOf(restoreTarget)) return t;
		}
		return null;
	}

	private void resumeCompletion() {
		BranchPointSet clashCause = Collections.min(m_ClashCauses, BranchPointSet.ORDER_BY_LATEST_BRANCH_POINT);
		m_ClashCauses.clear();
		if (clashCause.isEmpty()) return;
		
		m_InterTableauMan.pruneTo(clashCause.getLatestBranchPoint());

		TableauManager resumeTab = findOwnerOf(clashCause);
		BranchToken token = BranchToken.make(clashCause.getLatestBranchPoint());
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("All prunings completed");
			LOGGER.debug("Resuming completion on " + resumeTab.getPackage().toDebugString() + "with token " + token);
		}
		
		resumeTab.receiveToken(token);
		resumeTab.tryNextChoiceOnClashedBranchWith(clashCause);
	}

	private void completeAll() {
		boolean hasChanged = true;
		while (hasChanged) {
			if (isSynchronizingForClash() && !hasPendingMessages()) {
				resumeCompletion();
			}			
			
			hasChanged = false;
			for (TableauManager tab : m_Tableaux) {
				if (!tab.isComplete()) {
					tab.run();
					hasChanged = true;
				}
			}
		}
	}

}