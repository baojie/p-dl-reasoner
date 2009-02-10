package edu.iastate.pdlreasoner.master;

import static edu.iastate.pdlreasoner.model.ModelFactory.makeAllValues;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeAnd;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeAtom;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeNegation;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeOr;
import static edu.iastate.pdlreasoner.model.ModelFactory.makePackageID;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeRole;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeSomeValues;
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
import edu.iastate.pdlreasoner.model.AllValues;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.model.Role;
import edu.iastate.pdlreasoner.model.SomeValues;
import edu.iastate.pdlreasoner.model.Top;

public class TableauMasterSinglePackageTest {

	private PackageID pID;
	private OntologyPackage p;
	private Top top;
	private Atom[] atoms;
	private Negation[] negatedAtoms;
	private Role role;
	
	@Before
	public void setUp() {
		pID = makePackageID(URI.create("p"));
		p = new OntologyPackage(pID);
		top = makeTop(pID);
		atoms = new Atom[10];
		for (int i = 0; i < atoms.length; i++) {
			atoms[i] = makeAtom(pID, "atom" + i);
		}
		negatedAtoms = new Negation[atoms.length];
		for (int i = 0; i < negatedAtoms.length; i++) {
			negatedAtoms[i] = makeNegation(pID, atoms[i]);
		}
		role = makeRole(URI.create("r"));
	}
	
	private boolean runQuery(Concept satConcept) {
		Query query = new Query(new Ontology(p), null, satConcept, pID);
		PDLReasonerCentralizedWrapper reasoner = new PDLReasonerCentralizedWrapper();
		return reasoner.run(query).isSatisfiable();
	}
	
	@Test
	public void empty() {
		assertTrue(runQuery(top));
	}

	@Test
	public void bottom1() {
		p.addAxiom(top, Bottom.INSTANCE);
		assertFalse(runQuery(top));
	}
	
	@Test
	public void nestedClashesWithBottom() {
		p.addAxiom(top, atoms[0]);
		p.addAxiom(atoms[0], Bottom.INSTANCE);
		assertFalse(runQuery(top));
	}
	
	@Test
	public void nestedClashesWithNegatedAtom() {
		And bottom = makeAnd(atoms[1], negatedAtoms[1]);
		p.addAxiom(atoms[0], bottom);
		assertFalse(runQuery(atoms[0]));
		assertTrue(runQuery(atoms[1]));
	}

	@Test
	public void and_Or1() {
		Or negatedAtom0or1 = makeOr(negatedAtoms[0], negatedAtoms[1]);
		p.addAxiom(top, negatedAtom0or1);
		And atom0and1 = makeAnd(atoms[0], atoms[1]);
		assertFalse(runQuery(atom0and1));
	}

	@Test
	public void and_Or2() {
		p.addAxiom(top, atoms[0]);
		And bottom = makeAnd(atoms[0], negatedAtoms[0]);
		Or or = makeOr(bottom, atoms[1]);
		assertTrue(runQuery(or));
	}

	@Test
	public void nestedOr() {
		Or atom01or2 = makeOr(makeAnd(atoms[0], atoms[1]), atoms[2]);
		Or negatedAtom0or1 = makeOr(negatedAtoms[0], negatedAtoms[1]);
		p.addAxiom(top, atom01or2);
		p.addAxiom(top, negatedAtom0or1);
		assertTrue(runQuery(top));
	}

	@Test
	public void some1() {
		AllValues all = makeAllValues(role, negatedAtoms[0]);
		p.addAxiom(top, all);
		SomeValues some = makeSomeValues(role, atoms[0]);
		assertFalse(runQuery(some));
	}

	@Test
	public void some2() {
		AllValues all = makeAllValues(role, negatedAtoms[0]);
		SomeValues some = makeSomeValues(role, atoms[0]);
		And and = makeAnd(all, makeAnd(top, some));
		assertFalse(runQuery(and));
	}

	@Test
	public void some_Or() {
		AllValues all0 = makeAllValues(role, negatedAtoms[0]);
		AllValues all1 = makeAllValues(role, negatedAtoms[1]);
		p.addAxiom(top, all0);
		p.addAxiom(top, all1);
		Or atom0or1 = makeOr(atoms[0], atoms[1]);
		SomeValues some = makeSomeValues(role, atom0or1);
		assertFalse(runQuery(some));
	}

	@Test
	public void directSubsetBlocking1() {
		p.addAxiom(top, makeSomeValues(role, atoms[0]));
		assertTrue(runQuery(top));
	}

	@Test
	public void directSubsetBlocking2() {
		And and = makeAnd(makeSomeValues(role, atoms[0]), makeSomeValues(role, atoms[1]));
		p.addAxiom(top, and);
		assertTrue(runQuery(top));
	}
	
	@Test
	public void directSubsetBlockingWithBacktracking() {
		p.addAxiom(atoms[0], makeSomeValues(role, atoms[1]));
		p.addAxiom(atoms[1], makeSomeValues(role, atoms[0]));
		And and = makeAnd(atoms[0], atoms[1]);
		assertTrue(runQuery(and));
	}

	@Test
	public void indirectSubsetBlocking() {
		SomeValues some = makeSomeValues(role, makeSomeValues(role, atoms[0]));
		And and = makeAnd(makeSomeValues(role, atoms[0]), atoms[1]);
		assertTrue(runQuery(makeAnd(some, and)));
	}

	@Test
	public void contractictionAtChild() {
		p.addAxiom(top, makeSomeValues(role, atoms[0]));
		p.addAxiom(atoms[0], atoms[1]);
		AllValues all = makeAllValues(role, negatedAtoms[1]);
		assertFalse(runQuery(all));
	}

	@Test
	public void someValuesClash() {
		p.addAxiom(atoms[0], makeSomeValues(role, atoms[1]));
		p.addAxiom(makeSomeValues(role, top), negatedAtoms[0]);
		assertFalse(runQuery(atoms[0]));
	}
	
	@Test
	public void reopenParentAllValuesOnRestore() {
		And bottom = makeAnd(atoms[0], negatedAtoms[0]);
		SomeValues some = makeSomeValues(role, makeOr(bottom, atoms[0]));
		AllValues all = makeAllValues(role, bottom);
		And wrapAll = makeAnd(atoms[2], makeAnd(atoms[1], all));
		And and = makeAnd(some, wrapAll);
		assertFalse(runQuery(and));
	}
	
}

