package edu.iastate.pdlreasoner.tableau;

import edu.iastate.pdlreasoner.kb.KnowledgeBase;
import edu.iastate.pdlreasoner.kb.TBox;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.server.TableauServer;

public class TableauManager {
	
	private DLPackage m_Package;
	private TBox m_TBox;
	private TableauServer m_Server;
	private ABox m_ABox;
	
	public TableauManager(KnowledgeBase kb) {
		m_Package = kb.getPackage();
		m_TBox = kb.getTBox();
		m_ABox = new ABox();
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
		m_ABox.addNodeWith(c);
	}
	
	public void complete() {
		
	}
	
}
