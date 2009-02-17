package edu.iastate.pdlreasoner.kb.owlapi;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLOntologyManager;

public class OWLUtil {
	
	private static OWLOntologyManager Manager = OWLManager.createOWLOntologyManager();
	public static OWLDataFactory Factory = Manager.getOWLDataFactory();
		
}
