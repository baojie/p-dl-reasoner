package edu.iastate.pdlreasoner.model;

public interface Concept {
	public void accept(ConceptVisitor visitor);
}
