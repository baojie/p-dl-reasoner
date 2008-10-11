package edu.iastate.pdlreasoner.server;

import java.util.ArrayList;
import java.util.List;

import edu.iastate.pdlreasoner.kb.KnowledgeBase;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.ModelFactory;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Top;
import edu.iastate.pdlreasoner.struct.ImportGraph;

public class TableauServer {
	
	private List<KnowledgeBase> m_KBs;
	private ImportGraph m_Import;
	
	public TableauServer() {
		m_KBs = new ArrayList<KnowledgeBase>();
		m_Import = new ImportGraph();
	}
	
	public void addKnowledgeBase(KnowledgeBase kb) {
		m_KBs.add(kb);
	}
	
	public boolean isSatisfiable(Concept c, DLPackage witness) {
		return false;
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
	
}