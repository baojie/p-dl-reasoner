package edu.iastate.pdlreasoner.model;

public interface Concept {
	
	void accept(ConceptVisitor visitor);
	
}
