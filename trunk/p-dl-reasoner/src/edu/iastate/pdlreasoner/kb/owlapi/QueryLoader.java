package edu.iastate.pdlreasoner.kb.owlapi;

import static edu.iastate.pdlreasoner.model.ModelFactory.makeAllValues;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeAnd;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeAtom;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeNegation;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeOr;
import static edu.iastate.pdlreasoner.model.ModelFactory.makePackageID;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeRole;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeSomeValues;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeTop;

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
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.ModelFactory;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.model.Role;
import edu.iastate.pdlreasoner.model.Top;
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
			Concept sub = m_ConceptConverter.convert(axiom.getSubClass());
			Concept sup = m_ConceptConverter.convert(axiom.getSuperClass());
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

	private static Query getExample1() {
		PackageID[] p;
		OntologyPackage[] packages;
		
		p = new PackageID[3];
		for (int i = 0; i < p.length; i++) {
			p[i] = makePackageID(URI.create("#package" + i));
		}
		
		packages = new OntologyPackage[p.length];
		for (int i = 0; i < packages.length; i++) {
			packages[i] = new OntologyPackage(p[i]);
		}
		
		Top p0Top = makeTop(p[0]);
		Role r = makeRole(URI.create("#r"));
		Atom p0C = makeAtom(p[0], "C");
		
		Atom p1D1 = makeAtom(p[1], "D1");
		Atom p1D2 = makeAtom(p[1], "D2");
		Atom p1D3 = makeAtom(p[1], "D3");
		
		packages[0].addAxiom(p0Top, p1D3);
		
		And bigAnd = makeAnd(
				p1D1,
				makeSomeValues(r, p0C),
				makeAllValues(r, makeNegation(p[0], p0C))
			);
		Or bigOr = makeOr(bigAnd, makeNegation(p[1], p1D2));
		packages[0].addAxiom(p0Top, bigOr);
		
		packages[1].addAxiom(p1D1, p1D2);

		return new Query(new Ontology(packages), p0Top, p[0]);
	}
	
	private static Query getExample2() {
		PackageID[] p;
		OntologyPackage[] packages;
		
		p = new PackageID[3];
		for (int i = 0; i < p.length; i++) {
			p[i] = makePackageID(URI.create("#package" + i));
		}
		
		packages = new OntologyPackage[p.length];
		for (int i = 0; i < packages.length; i++) {
			packages[i] = new OntologyPackage(p[i]);
		}

		Atom p0A = makeAtom(p[0], "A");
		Atom p0B = makeAtom(p[0], "B");
		Atom p1C = makeAtom(p[1], "C");
		Atom p2D = makeAtom(p[2], "D");
		
		packages[0].addAxiom(p0A, p0B);
		packages[1].addAxiom(p0B, p1C);
		packages[2].addAxiom(p1C, p2D);

		Negation notSup = makeNegation(p[2], p2D);
		And sat = makeAnd(p0A, notSup);
		return new Query(new Ontology(packages), sat, p[2]);
	}
	
	private static Query getExample3() {
		PackageID[] p;
		OntologyPackage[] packages;
		
		p = new PackageID[3];
		for (int i = 0; i < p.length; i++) {
			p[i] = makePackageID(URI.create("#package" + i));
		}
		
		packages = new OntologyPackage[p.length];
		for (int i = 0; i < packages.length; i++) {
			packages[i] = new OntologyPackage(p[i]);
		}

		Atom p0B = makeAtom(p[0], "B");
		Atom p0F = makeAtom(p[0], "F");
		Atom p1P = makeAtom(p[1], "P");
		
		packages[0].addAxiom(p0B, p0F);
		packages[1].addAxiom(p1P, p0B);
		packages[1].addAxiom(p1P, makeNegation(p[1], p0F));
		
		return new Query(new Ontology(packages), p1P, p[1]);
	}
	
	private static Query getExample4a() {
		PackageID[] p;
		OntologyPackage[] packages;
		
		p = new PackageID[3];
		for (int i = 0; i < p.length; i++) {
			p[i] = makePackageID(URI.create("#package" + i));
		}
		
		packages = new OntologyPackage[p.length];
		for (int i = 0; i < packages.length; i++) {
			packages[i] = new OntologyPackage(p[i]);
		}

		Atom p0A = makeAtom(p[0], "A");
		Atom p0C = makeAtom(p[0], "C");
		Role p1r = makeRole(URI.create("#r"));
		Atom p1B = makeAtom(p[1], "B");
		
		packages[0].addAxiom(p0A, p0C);
		packages[1].addAxiom(p0A, makeSomeValues(p1r, p1B));
		packages[1].addAxiom(p1B, makeAnd(p0A, makeNegation(p[1], p0C)));
		
		return new Query(new Ontology(packages), p0A, p[0]);
	}

	private static Query getExample4b() {
		PackageID[] p;
		OntologyPackage[] packages;
		
		p = new PackageID[3];
		for (int i = 0; i < p.length; i++) {
			p[i] = makePackageID(URI.create("#package" + i));
		}
		
		packages = new OntologyPackage[p.length];
		for (int i = 0; i < packages.length; i++) {
			packages[i] = new OntologyPackage(p[i]);
		}

		Atom p0A = makeAtom(p[0], "A");
		Atom p0C = makeAtom(p[0], "C");
		Role p1r = makeRole(URI.create("#r"));
		Atom p1B = makeAtom(p[1], "B");
		
		packages[0].addAxiom(p0A, p0C);
		packages[1].addAxiom(p0A, makeSomeValues(p1r, p1B));
		packages[1].addAxiom(p1B, makeAnd(p0A, makeNegation(p[1], p0C)));
		
		return new Query(new Ontology(packages), p0A, p[1]);
	}

}
