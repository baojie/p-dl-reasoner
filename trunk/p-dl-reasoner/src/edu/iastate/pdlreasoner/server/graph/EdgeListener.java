package edu.iastate.pdlreasoner.server.graph;

import java.util.List;

public interface EdgeListener<E> {

	void edgesAdded(List<E> newEdges);

}
