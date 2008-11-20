package edu.iastate.pdlreasoner.server;

import java.util.List;
import java.util.Set;

import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.server.graph.GlobalNodeID;
import edu.iastate.pdlreasoner.server.graph.InterTableauGraph;
import edu.iastate.pdlreasoner.tableau.TableauManager;
import edu.iastate.pdlreasoner.tableau.TracedConcept;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;
import edu.iastate.pdlreasoner.tableau.message.BackwardConceptReport;
import edu.iastate.pdlreasoner.tableau.message.ForwardConceptReport;

public class InterTableauManager {

	//Constants
	private ImportGraph m_ImportGraph;
	private TableauTopology m_Tableaux;
	
	//Variables
	private InterTableauGraph m_InterTableau;

	public InterTableauManager(ImportGraph importGraph, TableauTopology tableaux) {
		m_ImportGraph = importGraph;
		m_Tableaux = tableaux;
		m_InterTableau = new InterTableauGraph();
	}

	public void processConceptReport(BackwardConceptReport backward) {
		GlobalNodeID requestedImportSource = backward.getImportSource();
		DLPackage importSourcePackage = requestedImportSource.getPackage();
		GlobalNodeID importTarget = backward.getImportTarget();
		TracedConcept concept = backward.getConcept();
		BranchPointSet dependency = concept.getDependency();
		
		m_InterTableau.addVertex(importTarget, dependency);
		
		GlobalNodeID importSource = m_InterTableau.getSourceVertexOf(importTarget, importSourcePackage);
		if (importSource == null) {
			TableauManager importSourceTab = m_Tableaux.get(importSourcePackage);
			importSource = importSourceTab.addRoot(dependency);
			m_InterTableau.addVertex(importSource, dependency);
			m_InterTableau.addEdge(importSource, importTarget);
			
			doRRule(importSource, importTarget, dependency);
		}
		
		requestedImportSource.copyIDFrom(importSource);
		m_Tableaux.get(importSourcePackage).receive(backward);
	}

	public void processConceptReport(ForwardConceptReport forward) {
		DLPackage msgTarget = forward.getImportTarget().getPackage();
		m_Tableaux.get(msgTarget).receive(forward);
	}

	private void doRRule(GlobalNodeID importSource, GlobalNodeID importTarget, BranchPointSet dependency) {
		Set<DLPackage> midPackages = m_ImportGraph.getAllVerticesConnecting(importSource.getPackage(), importTarget.getPackage());
		List<DLPackage> sortedMidPackages = m_ImportGraph.topologicalSort(midPackages);
		for (int i = 0; i < sortedMidPackages.size(); i++) {
			DLPackage midPackage = sortedMidPackages.get(i);
			GlobalNodeID midNode = m_InterTableau.getSourceVertexOf(importTarget, midPackage);
			if (midNode == null) {
				
			}
		}
	}

}
