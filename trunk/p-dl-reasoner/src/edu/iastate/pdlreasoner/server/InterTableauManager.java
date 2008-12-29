package edu.iastate.pdlreasoner.server;

import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;

import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.server.graph.EdgeListener;
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
		m_InterTableau.addEdgeListener(new EdgeListenerImp());
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
			targetDependency = conceptDependency;
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
			m_InterTableau.addEdgeAndCloseTransitivity(importSource, importTarget);
		}
		
		//Continue with reporting
		requestedImportSource.copyIDFrom(importSource);
		m_Tableaux.get(importSourcePackage).receive(backward);
	}

	public void processConceptReport(ForwardConceptReport forward) {
		DLPackage msgTarget = forward.getImportTarget().getPackage();
		m_Tableaux.get(msgTarget).receive(forward);
	}

	
	class EdgeListenerImp implements EdgeListener<DefaultEdge> {

		@Override
		public void edgesAdded(List<DefaultEdge> newEdges) {
			for (DefaultEdge e : newEdges) {
				GlobalNodeID importSource = m_InterTableau.getEdgeSource(e);
				GlobalNodeID importTarget = m_InterTableau.getEdgeTarget(e);
				BranchPointSet targetDependency = m_InterTableau.getDependency(importTarget);
				doRRule(importSource, importTarget, targetDependency);
			}
		}
		
		private void doRRule(GlobalNodeID importSource, GlobalNodeID importTarget, BranchPointSet dependency) {
			Set<DLPackage> midPackages = m_ImportGraph.getAllVerticesConnecting(importSource.getPackage(), importTarget.getPackage());
			
			
			
		}
		
	}
	
}
