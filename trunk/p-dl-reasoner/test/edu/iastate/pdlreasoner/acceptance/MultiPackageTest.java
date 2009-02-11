package edu.iastate.pdlreasoner.acceptance;

import static edu.iastate.pdlreasoner.model.ModelFactory.makeAtom;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeOr;
import static edu.iastate.pdlreasoner.model.ModelFactory.makePackageID;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeTop;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import edu.iastate.pdlreasoner.PDLReasonerCentralizedWrapper;
import edu.iastate.pdlreasoner.kb.Ontology;
import edu.iastate.pdlreasoner.kb.OntologyPackage;
import edu.iastate.pdlreasoner.kb.Query;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.model.Top;

public class MultiPackageTest {

	private PackageID[] pID;
	private OntologyPackage[] p;
	private Top[] top;
	
	@Before
	public void setUp() {
		pID = new PackageID[3];
		for (int i = 0; i < pID.length; i++) {
			pID[i] = makePackageID(URI.create("p" + i));
		}
		
		p = new OntologyPackage[pID.length];
		for (int i = 0; i < p.length; i++) {
			p[i] = new OntologyPackage(pID[i]);
		}
		
		top = new Top[pID.length];
		for (int i = 0; i < p.length; i++) {
			top[i] = makeTop(pID[i]);
		}
	}
	
	private boolean runQuery(Concept satConcept, PackageID witness) {
		Query query = new Query(new Ontology(p), null, satConcept, witness);
		PDLReasonerCentralizedWrapper reasoner = new PDLReasonerCentralizedWrapper();
		return reasoner.run(query).isSatisfiable();
	}

	@Test
	public void empty() {
		assertTrue(runQuery(top[0],pID[0]));
		assertTrue(runQuery(top[1],pID[1]));
	}

	@Test
	public void pruneInterTableauxOnClash() {
		Atom p0A = makeAtom(pID[0], "A");
		Atom p0B = makeAtom(pID[0], "B");
		
		p[0].addAxiom(p0A, Bottom.INSTANCE);
		p[0].addAxiom(p0B, Bottom.INSTANCE);
		p[1].addAxiom(Bottom.INSTANCE, p0A);
		
		Or query = makeOr(p0A, p0B);
		assertFalse(runQuery(query, pID[0]));
		assertFalse(runQuery(query, pID[1]));
	}

}