package edu.iastate.pdlreasoner.kb.owlapi;

import java.net.URI;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;

import edu.iastate.pdlreasoner.kb.Query;

public class QueryLoader {

	public static Query loadQuery(URI queryURI) throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology queryOntology = manager.loadOntology(queryURI);
		System.out.println(queryOntology.getAxioms());
		return null;
	}
	
}
