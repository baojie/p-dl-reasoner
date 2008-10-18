package edu.iastate.pdlreasoner.server;

import java.util.HashMap;
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
import edu.iastate.pdlreasoner.tableau.TableauManager;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TableauServer {
	
	private List<KnowledgeBase> m_KBs;
	private ImportGraph m_Import;
	private Map<DLPackage, TableauManager> m_Tableaux;
	
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
		completeAll();
		return !hasClash();
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
		m_Tableaux = new HashMap<DLPackage, TableauManager>();
		for (KnowledgeBase kb : m_KBs) {
			TableauManager tableau = kb.getTableau();
			tableau.setServer(this);
			m_Tableaux.put(kb.getPackage(), tableau);
		}
	}

	private boolean hasClash() {
		for (TableauManager t : m_Tableaux.values()) {
			if (!t.hasClash()) return true;
		}
		return false;
	}

	private void completeAll() {
		boolean hasChanged = true;
		while (hasChanged) {
			hasChanged = false;
			for (TableauManager tab : m_Tableaux.values()) {
				if (!tab.isComplete()) {
					tab.expand();
					hasChanged = true;
				}
			}
		}
	}
	
}