package edu.iastate.pdlreasoner.tableau;

import edu.iastate.pdlreasoner.kb.KnowledgeBase;
import edu.iastate.pdlreasoner.kb.TBox;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.server.TableauServer;

public class TableauManager {
	
	private TableauServer m_Server;
	private DLPackage m_Package;
	private TBox m_TBox;
	private TableauGraph m_Graph;
	
	public TableauManager(KnowledgeBase kb) {
		m_Package = kb.getPackage();
		m_TBox = kb.getTBox();
		m_Graph = new TableauGraph();
	}
	
	public void setServer(TableauServer server) {
		m_Server = server;
	}

	public boolean isComplete() {
		return m_Graph.getOpenNodes().isEmpty();
	}
	
	public boolean hasClash() {
		return m_Graph.hasClash();
	}

	public void addRootWith(Concept c) {
		Node root = m_Graph.makeRoot();
		root.addLabel(c);
		applyUniversalRestriction(root);
	}
	
	public void expand() {
		
	}

	private void applyUniversalRestriction(Node n) {
		for (Concept uc : m_TBox.getUC()) {
			n.addLabel(uc);
		}
	}

}
