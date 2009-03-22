package edu.iastate.pdlreasoner.kb.owlapi;

import java.net.URI;
import java.util.Set;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.AxiomType;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLSubClassAxiom;

import edu.iastate.pdlreasoner.exception.IllegalQueryException;
import edu.iastate.pdlreasoner.exception.OWLDescriptionNotSupportedException;
import edu.iastate.pdlreasoner.kb.Query;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.ModelFactory;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.PackageID;

public class QueryLoader {
	
	private OntologyPackageIDStore m_PackageStore;
	private ConceptConverter m_ConceptConverter;
	
	public QueryLoader() {
		m_PackageStore = new OntologyPackageIDStore();
		m_ConceptConverter = new ConceptConverter(m_PackageStore);
	}

	public Query loadQuery(URI queryURI, URI witnessURI) throws OWLOntologyCreationException, IllegalQueryException, OWLDescriptionNotSupportedException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology queryOntology = manager.loadOntology(queryURI);
		
		PackageID witnessID = m_PackageStore.getPackageID(witnessURI);
		OWLSubClassAxiom axiom = loadQueryAxiom(queryOntology);
		Concept satConcept = loadSatConcept(axiom, witnessID);
		return new Query(axiom, satConcept, witnessID);
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
