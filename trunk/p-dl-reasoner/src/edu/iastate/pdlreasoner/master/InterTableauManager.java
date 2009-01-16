package edu.iastate.pdlreasoner.master;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jgrapht.graph.DefaultEdge;
import org.jgroups.ChannelClosedException;
import org.jgroups.ChannelNotConnectedException;

import edu.iastate.pdlreasoner.kb.ImportGraph;
import edu.iastate.pdlreasoner.master.graph.GlobalNodeID;
import edu.iastate.pdlreasoner.master.graph.InterTableauTransitiveGraph;
import edu.iastate.pdlreasoner.message.BackwardConceptReport;
import edu.iastate.pdlreasoner.message.ForwardConceptReport;
import edu.iastate.pdlreasoner.message.MakePreImage;
import edu.iastate.pdlreasoner.message.ReopenAtoms;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.struct.CounterMap;
import edu.iastate.pdlreasoner.tableau.TracedConcept;
import edu.iastate.pdlreasoner.tableau.branch.BranchPoint;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSet;

public class InterTableauManager {

	private static final Logger LOGGER = Logger.getLogger(InterTableauManager.class);

	//Constants
	private TableauMaster m_TableauMaster;
	private ImportGraph m_ImportGraph;
	
	//Variables
	private InterTableauTransitiveGraph m_InterTableau;
	private CounterMap<PackageID> m_NodeIDCounters;

	public InterTableauManager(TableauMaster tableauMaster, ImportGraph importGraph) {
		m_TableauMaster = tableauMaster;
		m_ImportGraph = importGraph;
		m_InterTableau = new InterTableauTransitiveGraph();
		m_NodeIDCounters = new CounterMap<PackageID>();
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
		GlobalNodeID importSource = m_InterTableau.getSourceVertexOf(importTarget, importSourcePackageID);
		if (importSource == null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Creating new root on the source package " + importSourcePackageID);
			}
			
			importSource = makeGlobalNodeID(importSourcePackageID);
			m_TableauMaster.send(importSourcePackageID, new MakePreImage(importSource, sourceDependency)); 
			m_InterTableau.addVertex(importSource, sourceDependency);
			addEdge(importSource, importTarget);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Identified import source = " + importSource);
		}

		//Continue with reporting
		requestedImportSource.copyLocalIDFrom(importSource);
		m_TableauMaster.send(importSourcePackageID, backward);
	}

	public void processConceptReport(ForwardConceptReport forward) {
		GlobalNodeID requestedImportTarget = forward.getImportTarget();
		PackageID importTargetPackageID = requestedImportTarget.getPackageID();
		GlobalNodeID importSource = forward.getImportSource();
		
		GlobalNodeID importTarget = m_InterTableau.getTargetVertexOf(importSource, importTargetPackageID);
		if (importTarget == null) return;
		
		requestedImportTarget.copyLocalIDFrom(importTarget);
		m_TableauMaster.send(importTargetPackageID, forward);
	}
	
	public void pruneTo(BranchPoint restoreTarget) throws ChannelNotConnectedException, ChannelClosedException {
		m_InterTableau.pruneTo(restoreTarget);
		
		for (Entry<PackageID, Set<GlobalNodeID>> entry : m_InterTableau.getVerticesByPackage().entrySet()) {
			m_TableauMaster.send(entry.getKey(), new ReopenAtoms(entry.getValue()));
		}
	}
	
	private GlobalNodeID makeGlobalNodeID(PackageID packageID) {
		int nodeID = m_NodeIDCounters.next(packageID);
		return GlobalNodeID.make(packageID, false, nodeID);
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

				midNode = makeGlobalNodeID(midPackageID);
				m_TableauMaster.send(midPackageID, new MakePreImage(midNode, dependency));
				m_InterTableau.addVertex(midNode, dependency);
				addEdge(midNode, importTarget);
			}

			addEdge(importSource, midNode);
		}
	}

}
