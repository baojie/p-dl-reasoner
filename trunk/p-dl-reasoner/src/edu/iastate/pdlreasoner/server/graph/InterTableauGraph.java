package edu.iastate.pdlreasoner.server.graph;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

public class InterTableauGraph extends SimpleDirectedGraph<Node,DefaultEdge> {

	private static final long serialVersionUID = 1L;

	public InterTableauGraph() {
		super(DefaultEdge.class);
	}

}
