package edu.iastate.pdlreasoner.server;

import java.util.List;

import org.jgrapht.graph.DefaultEdge;

import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.server.graph.GlobalNodeID;
import edu.iastate.pdlreasoner.server.graph.InterTableauTransitiveGraph;
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
	private InterTableauTransitiveGraph m_InterTableau;

	public InterTableauManager(ImportGraph importGraph, TableauTopology tableaux) {
		m_ImportGraph = importGraph;
		m_Tableaux = tableaux;
		m_InterTableau = new InterTableauTransitiveGraph();
	}

	public void processConceptReport(BackwardConceptReport backward) {
		GlobalNodeID requestedImportSource = backward.getImportSource();
		DLPackage importSourcePackage = requestedImportSource.getPackage();
		GlobalNodeID importTarget = backward.getImportTarget();
		TracedConcept concept = backward.getConcept();
		BranchPointSet conceptDependency = concept.getDependency();
		
		//Add target to graph
		//Make sure sourceDependency is as later as conceptDependency or targetDependency
		// - the source node won't exist if either the target or concept is nonexistent 
		BranchPointSet sourceDependency = null;
		BranchPointSet targetDependency = m_InterTableau.getDependency(importTarget);
		if (targetDependency == null) {
			m_InterTableau.addVertex(importTarget, conceptDependency);
			sourceDependency = conceptDependency;
		} else {
			sourceDependency = BranchPointSet.union(targetDependency, conceptDependency);
		}
		
		//Add source to graph
		GlobalNodeID importSource = m_InterTableau.getSourceVertexOf(importTarget, importSourcePackage);
		if (importSource == null) {
			TableauManager importSourceTab = m_Tableaux.get(importSourcePackage);
			importSource = importSourceTab.addRoot(sourceDependency);
			m_InterTableau.addVertex(importSource, sourceDependency);
			addEdge(importSource, importTarget);
		}
		
		//Continue with reporting
		requestedImportSource.copyIDFrom(importSource);
		m_Tableaux.get(importSourcePackage).receive(backward);
	}

	public void processConceptReport(ForwardConceptReport forward) {
		DLPackage msgTarget = forward.getImportTarget().getPackage();
		m_Tableaux.get(msgTarget).receive(forward);
	}
	
	private void addEdge(GlobalNodeID importSource, GlobalNodeID importTarget) {
		List<DefaultEdge> newEdges = m_InterTableau.addEdgeAndCloseTransitivity(importSource, importTarget);
		for (DefaultEdge e : newEdges) {
			GlobalNodeID eSource = m_InterTableau.getEdgeSource(e);
			GlobalNodeID eTarget = m_InterTableau.getEdgeTarget(e);
			BranchPointSet eSourceDependency = m_InterTableau.getDependency(eSource);
			doRRule(eSource, eTarget, eSourceDependency);
		}
	}

	private void doRRule(GlobalNodeID importSource, GlobalNodeID importTarget, BranchPointSet dependency) {
		List<DLPackage> midPackages = m_ImportGraph.getAllVerticesConnecting(importSource.getPackage(), importTarget.getPackage());
		for (DLPackage midPackage : midPackages) {
			GlobalNodeID midNode = m_InterTableau.getSourceVertexOf(importTarget, midPackage);
			if (midNode == null) {
				TableauManager midTab = m_Tableaux.get(midPackage);
				midNode = midTab.addRoot(dependency);
				m_InterTableau.addVertex(midNode, dependency);
				addEdge(midNode, importTarget);
			}
			
			addEdge(importSource, midNode);
		}
	}

}
