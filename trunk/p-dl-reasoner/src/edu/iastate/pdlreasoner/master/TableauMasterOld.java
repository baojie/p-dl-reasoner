package edu.iastate.pdlreasoner.master;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.iastate.pdlreasoner.exception.IllegalQueryException;
import edu.iastate.pdlreasoner.kb.ImportGraph;
import edu.iastate.pdlreasoner.kb.OntologyPackage;
import edu.iastate.pdlreasoner.message.Clash;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.model.ModelFactory;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Top;
import edu.iastate.pdlreasoner.model.visitor.ExternalConceptsExtractor;
import edu.iastate.pdlreasoner.tableau.TableauManagerOld;
import edu.iastate.pdlreasoner.tableau.branch.BranchPoint;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;
import edu.iastate.pdlreasoner.tableau.branch.BranchToken;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TableauMasterOld {
	
	private static final Logger LOGGER = Logger.getLogger(TableauMasterOld.class);
	
	//Constants wrt KBs
	private List<OntologyPackage> m_Packages;
	private ImportGraph m_ImportGraph;
	
	//Variables (new per query)
	private boolean m_HasInitialized;
	private TableauTopology m_Tableaux;
	private InterTableauManager m_InterTableauMan;
	private Set<BranchPointSet> m_ClashCauses;
	
	public TableauMasterOld() {
		m_Packages = CollectionUtil.makeList();
	}
	
	
	//Upper Interfaces
	
//	public void addPackage(OntologyPackage pack) {
//		m_Packages.add(pack);
//	}
//	
//	public void init() {
//		for (OntologyPackage pack : m_Packages) {
//			pack.init();
//		}
//		
//		m_ImportGraph = new ImportGraph(m_Packages);
//		m_HasInitialized = true;
//		
//		if (LOGGER.isDebugEnabled()) {
//			LOGGER.debug("Import graph = " + m_ImportGraph);
//			LOGGER.debug("");
//		}
//	}

	public boolean isUnderstandableBy(Concept c, PackageID witness) {
		if (!m_HasInitialized) throw new IllegalStateException("TableauServer has not been initialized.");
		
		ExternalConceptsExtractor visitor = new ExternalConceptsExtractor(witness);
		c.accept(visitor);
		
		Set<PackageID> externals = CollectionUtil.makeSet();
		externals.addAll(visitor.getExternalConcepts().keySet());
		externals.addAll(visitor.getExternalNegationContexts());
		for (PackageID external : externals) {
			if (!m_ImportGraph.containsEdge(external, witness)) return false;
		}
		
		return true;
	}

	public boolean isSatisfiable(Concept c, PackageID witness) {
		if (!isUnderstandableBy(c, witness)) {
			throw new IllegalQueryException("Concept " + c + " is not understandable by " + witness);
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Query = satisfiability of " + c + " as witnessed by " + witness);
		}
		
		makeTableaux();
		TableauManagerOld witTableau = m_Tableaux.get(witness);
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
	
	public boolean isConsistent(PackageID witness) {
		Top topW = ModelFactory.makeTop(witness);
		return isSatisfiable(topW, witness);
	}
	
	public boolean isSubclassOf(Concept sub, Concept sup, PackageID witness) {
		Negation notSup = ModelFactory.makeNegation(witness, sup);
		And sat = ModelFactory.makeAnd(sub, notSup);
		return !isSatisfiable(sat, witness);
	}
	
	
	//Lower Interfaces
	
//	public void processClash(BranchPointSet clashCause) {
//		if (m_ClashCauses.add(clashCause)) {
//			Clash clash = new Clash(clashCause);
//			for (TableauManagerOld tab : m_Tableaux) {
//				tab.receive(clash);
//			}
//		}
//	}

//	public boolean isSynchronizingForClash() {
//		return !m_ClashCauses.isEmpty();
//	}
	
//	public void returnTokenFrom(TableauManagerOld tab, BranchToken token) {
//		if (isSynchronizingForClash()) return;
//		
//		TableauManagerOld next = m_Tableaux.getNext(tab);
//		next.receiveToken(token);
//	}
	
	
	//Private

//	private void makeTableaux() {
//		m_Tableaux = new TableauTopology(m_Packages);
//		m_InterTableauMan = new InterTableauManager(m_ImportGraph, m_Tableaux);
//		for (TableauManagerOld t : m_Tableaux) {
//			t.setMaster(this);
//			t.setImportGraph(m_ImportGraph);
//			t.setInterTableauManager(m_InterTableauMan);
//		}
//		
//		m_ClashCauses = CollectionUtil.makeSet();
//	}

	private boolean hasClashAtOrigin() {
		for (TableauManagerOld t : m_Tableaux) {
			if (t.hasClashAtOrigin()) return true;
		}
		return false;
	}
	
//	private boolean hasPendingMessages() {
//		for (TableauManagerOld t : m_Tableaux) {
//			if (t.hasPendingMessages()) return true;
//		}
//		return false;
//	}
	
	private TableauManagerOld findOwnerOf(BranchPointSet clashCause) {
		BranchPoint restoreTarget = clashCause.getLatestBranchPoint();
		for (TableauManagerOld t : m_Tableaux) {
			if (t.isOwnerOf(restoreTarget)) return t;
		}
		return null;
	}

//	private void resumeCompletion() {
//		BranchPointSet clashCause = Collections.min(m_ClashCauses, BranchPointSet.ORDER_BY_LATEST_BRANCH_POINT);
//		m_ClashCauses.clear();
//		if (clashCause.isEmpty()) return;
//		
//		m_InterTableauMan.pruneTo(clashCause.getLatestBranchPoint());
//
//		TableauManagerOld resumeTab = findOwnerOf(clashCause);
//		BranchToken token = BranchToken.make(clashCause.getLatestBranchPoint());
//		
//		if (LOGGER.isDebugEnabled()) {
//			LOGGER.debug("All prunings completed");
//			LOGGER.debug("Resuming completion on " + resumeTab.getPackageID().toDebugString() + "with token " + token);
//		}
//		
//		resumeTab.receiveToken(token);
//		resumeTab.tryNextChoiceOnClashedBranchWith(clashCause);
//	}

	private void completeAll() {
		boolean hasChanged = true;
		while (hasChanged) {
			if (isSynchronizingForClash() && !hasPendingMessages()) {
				resumeCompletion();
			}			
			
			hasChanged = false;
			for (TableauManagerOld tab : m_Tableaux) {
				if (!tab.isComplete()) {
					tab.run();
					hasChanged = true;
				}
			}
		}
	}

}