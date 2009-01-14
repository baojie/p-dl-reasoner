package edu.iastate.pdlreasoner.master;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jgrapht.graph.DefaultEdge;

import edu.iastate.pdlreasoner.kb.ImportGraph;
import edu.iastate.pdlreasoner.master.graph.GlobalNodeID;
import edu.iastate.pdlreasoner.master.graph.InterTableauTransitiveGraph;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.tableau.TableauManagerOld;
import edu.iastate.pdlreasoner.tableau.TracedConcept;
import edu.iastate.pdlreasoner.tableau.branch.BranchPoint;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;
import edu.iastate.pdlreasoner.tableau.message.BackwardConceptReport;
import edu.iastate.pdlreasoner.tableau.message.ForwardConceptReport;

public class InterTableauManager {

	private static final Logger LOGGER = Logger.getLogger(InterTableauManager.class);

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
		PackageID importSourcePackageID = requestedImportSource.getPackageID();
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
		TableauManagerOld importSourceTab = m_Tableaux.get(importSourcePackageID);
		GlobalNodeID importSource = m_InterTableau.getSourceVertexOf(importTarget, importSourcePackageID);
		if (importSource == null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Creating new root on the source package " + importSourceTab.getPackageID());
			}
			
			importSource = importSourceTab.addRoot(sourceDependency);
			m_InterTableau.addVertex(importSource, sourceDependency);
			addEdge(importSource, importTarget);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Identified import source = " + importSource);
		}

		//Continue with reporting
		requestedImportSource.copyIDFrom(importSource);
		importSourceTab.receive(backward);
	}

	public void processConceptReport(ForwardConceptReport forward) {
		GlobalNodeID requestedImportTarget = forward.getImportTarget();
		PackageID importTargetPackageID = requestedImportTarget.getPackageID();
		GlobalNodeID importSource = forward.getImportSource();
		
		GlobalNodeID importTarget = m_InterTableau.getTargetVertexOf(importSource, importTargetPackageID);
		if (importTarget == null) return;
		
		requestedImportTarget.copyIDFrom(importTarget);
		TableauManagerOld importTargetTab = m_Tableaux.get(importTargetPackageID);
		importTargetTab.receive(forward);
	}
	
	public void pruneTo(BranchPoint restoreTarget) {
		m_InterTableau.pruneTo(restoreTarget);
		
		for (Entry<PackageID, Set<GlobalNodeID>> entry : m_InterTableau.getVerticesByPackage().entrySet()) {
			TableauManagerOld tab = m_Tableaux.get(entry.getKey());
			tab.reopenAtomsOnGlobalNodes(entry.getValue());
		}
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
		List<PackageID> midPackageIDs = m_ImportGraph.getAllVerticesConnecting(importSource.getPackageID(), importTarget.getPackageID());
		for (PackageID midPackageID : midPackageIDs) {
			GlobalNodeID midNode = m_InterTableau.getSourceVertexOf(importTarget, midPackageID);
			if (midNode == null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Creating new root for R-Rule on the mid package " + midPackageID);
				}

				TableauManagerOld midTab = m_Tableaux.get(midPackageID);
				midNode = midTab.addRoot(dependency);
				m_InterTableau.addVertex(midNode, dependency);
				addEdge(midNode, importTarget);
			}

			addEdge(importSource, midNode);
		}
	}

}
