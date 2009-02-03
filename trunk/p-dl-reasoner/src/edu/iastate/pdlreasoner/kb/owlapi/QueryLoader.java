package edu.iastate.pdlreasoner.kb.owlapi;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.AxiomType;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLSubClassAxiom;

import edu.iastate.pdlreasoner.exception.IllegalQueryException;
import edu.iastate.pdlreasoner.exception.OWLDescriptionNotSupportedException;
import edu.iastate.pdlreasoner.kb.Ontology;
import edu.iastate.pdlreasoner.kb.OntologyPackage;
import edu.iastate.pdlreasoner.kb.Query;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.ModelFactory;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.util.CollectionUtil;
import edu.iastate.pdlreasoner.util.URIUtil;

public class QueryLoader {
	
	private OntologyPackageIDStore m_PackageStore;
	private ConceptConverter m_ConceptConverter;

	public Query loadQuery(URI queryURI) throws OWLOntologyCreationException, IllegalQueryException, OWLDescriptionNotSupportedException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology queryOntology = manager.loadOntology(queryURI);
		Set<OWLOntology> witnesses = manager.getImports(queryOntology);
		if (witnesses.size() != 1) throw new IllegalQueryException("Query ontology must import exactly one witness ontology.");
		OWLOntology witness = witnesses.iterator().next();
		
		Ontology ontology = loadOntology(manager, witness);
		PackageID witnessID = m_PackageStore.getPackageID(witness.getURI());
		Concept satConcept = loadQueryConcept(queryOntology, witnessID);
		return new Query(ontology, satConcept, witnessID);
	}

	private Ontology loadOntology(OWLOntologyManager manager, OWLOntology witness) throws OWLDescriptionNotSupportedException {
		Set<OWLOntology> importClosure = manager.getImportsClosure(witness);
		makePackageStore(importClosure);
		
		List<OntologyPackage> ontologyPackages = CollectionUtil.makeList();
		for (OWLOntology ontology : importClosure) {
			OntologyPackage ontologyPackage = loadOntologyPackage(ontology);
			ontologyPackages.add(ontologyPackage);
		}
		
		return new Ontology(ontologyPackages);
	}
	
	private void makePackageStore(Set<OWLOntology> ontologies) {
		m_PackageStore = new OntologyPackageIDStore();
		for (OWLOntology ontology : ontologies) {
			try {
				URI uri = URIUtil.filterFragment(ontology.getURI());
				m_PackageStore.addPackageID(uri);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return;
			}
		}
		
		m_ConceptConverter = new ConceptConverter(m_PackageStore);
	}
	
	private OntologyPackage loadOntologyPackage(OWLOntology ontology) throws OWLDescriptionNotSupportedException {
		PackageID packageID = m_PackageStore.getPackageID(ontology.getURI());
		m_ConceptConverter.setPackageID(packageID);
		
		OntologyPackage ontologyPackage = new OntologyPackage(packageID);
		for (OWLSubClassAxiom axiom : ontology.getAxioms(AxiomType.SUBCLASS)) {
			OWLDescription owlSub = axiom.getSubClass();
			OWLDescription owlSup = axiom.getSuperClass();
			if (owlSup.isOWLThing()) continue;
			
			Concept sub = m_ConceptConverter.convert(owlSub);
			Concept sup = m_ConceptConverter.convert(owlSup);
			ontologyPackage.addAxiom(sub, sup);
		}
		
		return ontologyPackage;
	}

	private Concept loadQueryConcept(OWLOntology queryOntology, PackageID witnessID) throws IllegalQueryException, OWLDescriptionNotSupportedException {
		m_ConceptConverter.setPackageID(witnessID);
		
		Set<OWLSubClassAxiom> axioms = queryOntology.getAxioms(AxiomType.SUBCLASS);
		if (axioms.size() != 1) throw new IllegalQueryException("Only one query concept is supported.");
		OWLSubClassAxiom axiom = axioms.iterator().next();
		Concept sub = m_ConceptConverter.convert(axiom.getSubClass());
		Concept sup = m_ConceptConverter.convert(axiom.getSuperClass());
		
		if (Bottom.INSTANCE.equals(sup)) {
			return sub;
		} else {
			Negation notSup = ModelFactory.makeNegation(witnessID, sup);
			return ModelFactory.makeAnd(sub, notSup);
		}
	}

}
