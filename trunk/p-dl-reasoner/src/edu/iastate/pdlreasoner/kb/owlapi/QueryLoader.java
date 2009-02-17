package edu.iastate.pdlreasoner.kb.owlapi;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.AxiomType;
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
	private OntologyConverter m_OntologyConverter;

	public Query loadQuery(URI queryURI) throws OWLOntologyCreationException, IllegalQueryException, OWLDescriptionNotSupportedException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology queryOntology = manager.loadOntology(queryURI);
		Set<OWLOntology> witnesses = manager.getImports(queryOntology);
		if (witnesses.size() != 1) throw new IllegalQueryException("Query ontology must import exactly one witness ontology.");
		OWLOntology witness = witnesses.iterator().next();
		
		Ontology ontology = loadOntology(manager, witness);
		PackageID witnessID = m_PackageStore.getPackageID(witness.getURI());
		OWLSubClassAxiom axiom = loadQueryAxiom(queryOntology);
		Concept satConcept = loadSatConcept(axiom, witnessID);
		return new Query(ontology, axiom, satConcept, witnessID);
	}

	private Ontology loadOntology(OWLOntologyManager manager, OWLOntology witness) throws OWLDescriptionNotSupportedException {
		Set<OWLOntology> importClosure = manager.getImportsClosure(witness);
		makePackageStore(importClosure);
		
		List<OntologyPackage> ontologyPackages = CollectionUtil.makeList();
		for (OWLOntology ontology : importClosure) {
			OntologyPackage ontologyPackage = m_OntologyConverter.convert(ontology);
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
		m_OntologyConverter = new OntologyConverter(m_PackageStore, m_ConceptConverter);
	}
	
	private OWLSubClassAxiom loadQueryAxiom(OWLOntology queryOntology) throws IllegalQueryException {
		Set<OWLSubClassAxiom> axioms = queryOntology.getAxioms(AxiomType.SUBCLASS);
		if (axioms.size() != 1) throw new IllegalQueryException("Only one axiom per query is supported.");
		return axioms.iterator().next();
	}

	private Concept loadSatConcept(OWLSubClassAxiom axiom, PackageID witnessID) throws OWLDescriptionNotSupportedException {
		m_ConceptConverter.setPackageID(witnessID);
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
