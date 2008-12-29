package edu.iastate.pdlreasoner.struct;

import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.SimpleDirectedGraph;

public class MultiLabeledDirectedGraph<V,L> extends SimpleDirectedGraph<V,MultiLabeledEdge<L>> {

	private static final long serialVersionUID = 1L;

	public MultiLabeledDirectedGraph() {
		super(new EdgeFactory<V,MultiLabeledEdge<L>>() {
				public MultiLabeledEdge<L> createEdge(V source, V target) {
					return new MultiLabeledEdge<L>();
				}
			});
	}
	
	public void addLabels(V source, V target, Set<L> labels) {
		addVertex(source);
		addVertex(target);
		MultiLabeledEdge<L> edge = getEdge(source, target);
		if (edge == null) {
			edge = addEdge(source, target);
		}
		
		edge.addLabels(labels);
	}
}