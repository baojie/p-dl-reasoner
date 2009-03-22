package edu.iastate.pdlreasoner.kb.owlapi;

import java.net.URI;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;

import edu.iastate.pdlreasoner.exception.OWLDescriptionNotSupportedException;
import edu.iastate.pdlreasoner.kb.OntologyPackage;

public class OntologyLoader {
	
	private OntologyPackageIDStore m_PackageStore;
	private ConceptConverter m_ConceptConverter;
	private OntologyConverter m_OntologyConverter;
	
	public OntologyLoader() {
		m_PackageStore = new OntologyPackageIDStore();
		m_ConceptConverter = new ConceptConverter(m_PackageStore);
		m_OntologyConverter = new OntologyConverter(m_PackageStore, m_ConceptConverter);
	}

	public OntologyPackage loadOntology(URI ontologyURI) throws OWLOntologyCreationException, OWLDescriptionNotSupportedException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(ontologyURI);
		return m_OntologyConverter.convert(ontology);
	}
	
}
