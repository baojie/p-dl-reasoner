package edu.iastate.pdlreasoner.struct;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;

public class ImportGraph extends SimpleDirectedGraph<DLPackage, DefaultEdge> {

	private static final long serialVersionUID = 1L;

	public ImportGraph() {
		super(ImportRelationEdge.class);
	}

	public static class ImportRelationEdge extends DefaultEdge {

		private static final long serialVersionUID = 1L;
		
		private Set<Concept> m_Concepts;
		
		public ImportRelationEdge() {
			m_Concepts = new HashSet<Concept>();
		}
		
		public void addConcept(Concept c) {
			m_Concepts.add(c);
		}
		
	}

}
