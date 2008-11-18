package edu.iastate.pdlreasoner.server;

import edu.iastate.pdlreasoner.tableau.message.BackwardConceptReport;
import edu.iastate.pdlreasoner.tableau.message.ForwardConceptReport;

public class InterTableauManager {

	private ImportGraph m_ImportGraph;
	private TableauTopology m_Tableaux;

	public InterTableauManager(ImportGraph importGraph, TableauTopology tableaux) {
		m_ImportGraph = importGraph;
		m_Tableaux = tableaux;
	}

	public void processConceptReport(BackwardConceptReport backward) {
		m_Tableaux.get(backward.getImporter()).receive(backward);
	}

	public void processConceptReport(ForwardConceptReport forward) {
		m_Tableaux.get(forward.getImporter()).receive(forward);
	}


	
}
