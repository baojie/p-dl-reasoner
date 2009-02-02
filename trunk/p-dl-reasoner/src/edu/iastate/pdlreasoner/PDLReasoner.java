package edu.iastate.pdlreasoner;

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

import org.jgroups.ChannelException;
import org.semanticweb.owl.model.OWLOntologyCreationException;

import edu.iastate.pdlreasoner.exception.NotEnoughSlavesException;
import edu.iastate.pdlreasoner.kb.Ontology;
import edu.iastate.pdlreasoner.kb.OntologyPackage;
import edu.iastate.pdlreasoner.kb.Query;
import edu.iastate.pdlreasoner.kb.QueryResult;
import edu.iastate.pdlreasoner.kb.owlapi.QueryLoader;
import edu.iastate.pdlreasoner.master.TableauMaster;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.model.Role;
import edu.iastate.pdlreasoner.model.Top;
import edu.iastate.pdlreasoner.tableau.Tableau;
import edu.iastate.pdlreasoner.util.URIUtil;


public class PDLReasoner {

	public static void main(String[] args) {
		boolean isMaster = false;
		if (args.length != 2) {
			printUsage();
			System.exit(1);
		}
		
		if ("-m".equalsIgnoreCase(args[0])) {
			isMaster = true;
		} else if ("-s".equalsIgnoreCase(args[0])) {
			isMaster = false;
		} else {
			printUsage();
			System.exit(1);
		}
		
		String queryPath = args[1];
		URI queryURI = URIUtil.toURI(queryPath);
		
		Query query = null;
		try {
			query = QueryLoader.loadQuery(queryURI);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		if (isMaster) {
			TableauMaster master = new TableauMaster();
			QueryResult result = null;
			
			try {
				result = master.run(query);
			} catch (ChannelException e) {
				e.printStackTrace();
			} catch (NotEnoughSlavesException e) {
				e.printStackTrace();
			}
			
			System.out.println(result);
		} else {
			Tableau slave = new Tableau();

			try {
				slave.run(query);
			} catch (ChannelException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void printUsage() {
		System.out.println("Usage: java PDLReasoner [-m|-s] query.owl");
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
