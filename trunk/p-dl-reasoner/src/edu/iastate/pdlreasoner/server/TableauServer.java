package edu.iastate.pdlreasoner.server;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.iastate.pdlreasoner.kb.KnowledgeBase;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.ModelFactory;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Top;
import edu.iastate.pdlreasoner.struct.ImportGraph;
import edu.iastate.pdlreasoner.struct.MultiValuedMap;
import edu.iastate.pdlreasoner.struct.Ring;
import edu.iastate.pdlreasoner.tableau.TableauManager;
import edu.iastate.pdlreasoner.tableau.branch.BranchPoint;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;
import edu.iastate.pdlreasoner.tableau.branch.BranchToken;
import edu.iastate.pdlreasoner.tableau.messaging.Clash;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TableauServer {
	
	private List<KnowledgeBase> m_KBs;
	private ImportGraph m_Import;
	private Map<DLPackage, TableauManager> m_Tableaux;
	private Ring<TableauManager> m_TableauxRing;
	private Set<BranchPointSet> m_ClashCauses;
	
	public TableauServer() {
		m_KBs = CollectionUtil.makeList();
		m_Import = new ImportGraph();
	}
	
	public void addKnowledgeBase(KnowledgeBase kb) {
		m_KBs.add(kb);
	}
	
	public void init() {
		for (KnowledgeBase kb : m_KBs) {
			kb.init();
		}
		
		buildImportGraph();
	}
	
	public boolean isSatisfiable(Concept c, DLPackage witness) {
		makeTableaux();
		TableauManager witTableau = m_Tableaux.get(witness);
		witTableau.addRootWith(c);
		witTableau.receiveToken(BranchToken.make());
		completeAll();
		return !hasClashAtOrigin();
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
	
	public void processClash(BranchPointSet clashCause) {
		if (m_ClashCauses.add(clashCause)) {
			Clash clash = new Clash(clashCause);
			for (TableauManager tab : m_Tableaux.values()) {
				tab.receive(clash);
			}
		}
	}

	public boolean isSynchronizingForClash() {
		return !m_ClashCauses.isEmpty();
	}
	
	public void returnTokenFrom(TableauManager tab, BranchToken token) {
		if (isSynchronizingForClash()) return;
		
		TableauManager next = m_TableauxRing.getNext(tab);
		next.receiveToken(token);
	}

	private void buildImportGraph() {
		for (KnowledgeBase kb : m_KBs) {
			DLPackage homePackage = kb.getPackage();
			MultiValuedMap<DLPackage, Concept> externalConcepts = kb.getExternalConcepts();
			for (Entry<DLPackage, Set<Concept>> entry : externalConcepts.entrySet()) {
				m_Import.addLabels(entry.getKey(), homePackage, entry.getValue());
			}
		}
	}
	
	private void makeTableaux() {
		m_Tableaux = CollectionUtil.makeMap();
		for (KnowledgeBase kb : m_KBs) {
			TableauManager tableau = kb.getTableau();
			tableau.setServer(this);
			m_Tableaux.put(kb.getPackage(), tableau);
		}

		m_TableauxRing = new Ring<TableauManager>(m_Tableaux.values());
		m_ClashCauses = CollectionUtil.makeSet();
	}

	private boolean hasClashAtOrigin() {
		for (TableauManager t : m_Tableaux.values()) {
			if (t.hasClashAtOrigin()) return true;
		}
		return false;
	}
	
	private boolean hasPendingMessages() {
		for (TableauManager t : m_Tableaux.values()) {
			if (t.hasPendingMessages()) return true;
		}
		return false;
	}
	
	private TableauManager findOwnerOf(BranchPointSet clashCause) {
		BranchPoint restoreTarget = clashCause.getLatestBranchPoint();
		for (TableauManager t : m_Tableaux.values()) {
			if (t.isOwnerOf(restoreTarget)) return t;
		}
		return null;
	}

	private void resumeCompletion() {
		BranchPointSet clashCause = Collections.min(m_ClashCauses, BranchPointSet.ORDER_BY_LATEST_BRANCH_POINT);
		m_ClashCauses.clear();
		if (clashCause.isEmpty()) return;
		
		TableauManager resumeTab = findOwnerOf(clashCause);
		BranchToken token = BranchToken.make(clashCause.getLatestBranchPoint());
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
			for (TableauManager tab : m_Tableaux.values()) {
				if (!tab.isComplete()) {
					tab.run();
					hasChanged = true;
				}
			}
		}
	}

}