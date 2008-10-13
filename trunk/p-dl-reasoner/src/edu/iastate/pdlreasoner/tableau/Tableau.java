package edu.iastate.pdlreasoner.tableau;

import edu.iastate.pdlreasoner.kb.KnowledgeBase;
import edu.iastate.pdlreasoner.kb.TBox;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.server.TableauServer;

public class Tableau {
	
	private DLPackage m_Package;
	private TBox m_TBox;
	private TableauServer m_Server;
	
	public Tableau(KnowledgeBase kb) {
		m_Package = kb.getPackage();
		m_TBox = kb.getTBox();
	}
	
	public void setServer(TableauServer server) {
		m_Server = server;
	}

	public boolean isComplete() {
		return false;
	}
	
	public boolean hasClash() {
		return false;
	}

	public void addNodeWith(Concept c) {
		
	}
	
	public void complete() {
		
	}
}
