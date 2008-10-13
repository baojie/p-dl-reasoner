package edu.iastate.pdlreasoner.server;

import java.util.ArrayList;
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
import edu.iastate.pdlreasoner.tableau.Tableau;

public class TableauServer {
	
	private List<KnowledgeBase> m_KBs;
	private ImportGraph m_Import;
	private Map<DLPackage, Tableau> m_Tableaux;
	
	public TableauServer() {
		m_KBs = new ArrayList<KnowledgeBase>();
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
		Tableau witTableau = m_Tableaux.get(witness);
		witTableau.addNodeWith(c);
		complete();
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
		m_Tableaux = new HashMap<DLPackage, Tableau>();
		for (KnowledgeBase kb : m_KBs) {
			Tableau tableau = kb.getTableau();
			tableau.setServer(this);
			m_Tableaux.put(kb.getPackage(), tableau);
		}
	}

	private boolean haveAllComplete() {
		for (Tableau t : m_Tableaux.values()) {
			if (!t.isComplete()) return false;
		}
		return true;
	}

	private boolean hasClash() {
		for (Tableau t : m_Tableaux.values()) {
			if (!t.hasClash()) return true;
		}
		return false;
	}

	private void complete() {
	}
	
}