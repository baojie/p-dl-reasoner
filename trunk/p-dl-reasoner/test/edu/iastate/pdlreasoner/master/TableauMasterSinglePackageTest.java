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
import static org.junit.Assert.fail;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import edu.iastate.pdlreasoner.kb.OntologyPackage;
import edu.iastate.pdlreasoner.master.TableauMasterOld;
import edu.iastate.pdlreasoner.model.AllValues;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.Role;
import edu.iastate.pdlreasoner.model.SomeValues;
import edu.iastate.pdlreasoner.model.Top;

public class TableauMasterSinglePackageTest {

	private TableauMasterOld m_TableauMaster;
	private PackageID p;
	private OntologyPackage kb;
	private Top top;
	private Atom[] atoms;
	private Negation[] negatedAtoms;
	private Role role;
	
	@Before
	public void setUp() {
		m_TableauMaster = new TableauMasterOld();
		p = makePackageID(URI.create("#package"));
		kb = new OntologyPackage(p);
		m_TableauMaster.addPackage(kb);
		top = makeTop(p);
		atoms = new Atom[10];
		for (int i = 0; i < atoms.length; i++) {
			atoms[i] = makeAtom(p, URI.create("#atom" + i));
		}
		negatedAtoms = new Negation[atoms.length];
		for (int i = 0; i < negatedAtoms.length; i++) {
			negatedAtoms[i] = makeNegation(p, atoms[i]);
		}
		role = makeRole(URI.create("#role"));
	}

	@Test
	public void uninitialized() {
		try {
			m_TableauMaster.isConsistent(p);
			fail("Expected IllegalStateException");
		} catch (IllegalStateException ex) {
		}
	}
	
	@Test
	public void empty() {
		m_TableauMaster.init();
		assertTrue(m_TableauMaster.isConsistent(p));
	}

	@Test
	public void bottom1() {
		kb.addAxiom(top, Bottom.INSTANCE);
		m_TableauMaster.init();
		assertFalse(m_TableauMaster.isConsistent(p));
	}
	
	@Test
	public void nestedClashesWithBottom() {
		kb.addAxiom(top, atoms[0]);
		kb.addAxiom(atoms[0], Bottom.INSTANCE);
		m_TableauMaster.init();
		assertFalse(m_TableauMaster.isConsistent(p));
	}
	
	@Test
	public void nestedClashesWithNegatedAtom() {
		And bottom = makeAnd(atoms[1], negatedAtoms[1]);
		kb.addAxiom(atoms[0], bottom);
		m_TableauMaster.init();
		assertFalse(m_TableauMaster.isSatisfiable(atoms[0], p));
		assertTrue(m_TableauMaster.isSatisfiable(atoms[1], p));
	}

	@Test
	public void and_Or1() {
		Or negatedAtom0or1 = makeOr(negatedAtoms[0], negatedAtoms[1]);
		kb.addAxiom(top, negatedAtom0or1);
		m_TableauMaster.init();
		And atom0and1 = makeAnd(atoms[0], atoms[1]);
		assertFalse(m_TableauMaster.isSatisfiable(atom0and1, p));
	}

	@Test
	public void and_Or2() {
		kb.addAxiom(top, atoms[0]);
		m_TableauMaster.init();
		And bottom = makeAnd(atoms[0], negatedAtoms[0]);
		Or or = makeOr(bottom, atoms[1]);
		assertTrue(m_TableauMaster.isSatisfiable(or, p));
	}

	@Test
	public void nestedOr() {
		Or atom01or2 = makeOr(makeAnd(atoms[0], atoms[1]), atoms[2]);
		Or negatedAtom0or1 = makeOr(negatedAtoms[0], negatedAtoms[1]);
		kb.addAxiom(top, atom01or2);
		kb.addAxiom(top, negatedAtom0or1);
		m_TableauMaster.init();
		assertTrue(m_TableauMaster.isConsistent(p));
	}

	@Test
	public void some1() {
		AllValues all = makeAllValues(role, negatedAtoms[0]);
		kb.addAxiom(top, all);
		m_TableauMaster.init();
		SomeValues some = makeSomeValues(role, atoms[0]);
		assertFalse(m_TableauMaster.isSatisfiable(some, p));
	}

	@Test
	public void some2() {
		m_TableauMaster.init();
		AllValues all = makeAllValues(role, negatedAtoms[0]);
		SomeValues some = makeSomeValues(role, atoms[0]);
		And and = makeAnd(all, makeAnd(top, some));
		assertFalse(m_TableauMaster.isSatisfiable(and, p));
	}

	@Test
	public void some_Or() {
		AllValues all0 = makeAllValues(role, negatedAtoms[0]);
		AllValues all1 = makeAllValues(role, negatedAtoms[1]);
		kb.addAxiom(top, all0);
		kb.addAxiom(top, all1);
		m_TableauMaster.init();
		Or atom0or1 = makeOr(atoms[0], atoms[1]);
		SomeValues some = makeSomeValues(role, atom0or1);
		assertFalse(m_TableauMaster.isSatisfiable(some, p));
	}

	@Test
	public void directSubsetBlocking1() {
		kb.addAxiom(top, makeSomeValues(role, atoms[0]));
		m_TableauMaster.init();
		assertTrue(m_TableauMaster.isConsistent(p));
	}

	@Test
	public void directSubsetBlocking2() {
		And and = makeAnd(makeSomeValues(role, atoms[0]), makeSomeValues(role, atoms[1]));
		kb.addAxiom(top, and);
		m_TableauMaster.init();
		assertTrue(m_TableauMaster.isConsistent(p));
	}
	
	@Test
	public void directSubsetBlockingWithBacktracking() {
		kb.addAxiom(atoms[0], makeSomeValues(role, atoms[1]));
		kb.addAxiom(atoms[1], makeSomeValues(role, atoms[0]));
		m_TableauMaster.init();
		And and = makeAnd(atoms[0], atoms[1]);
		assertTrue(m_TableauMaster.isSatisfiable(and, p));
	}

	@Test
	public void indirectSubsetBlocking() {
		m_TableauMaster.init();
		SomeValues some = makeSomeValues(role, makeSomeValues(role, atoms[0]));
		And and = makeAnd(makeSomeValues(role, atoms[0]), atoms[1]);
		assertTrue(m_TableauMaster.isSatisfiable(makeAnd(some, and), p));
	}

	@Test
	public void contractictionAtChild() {
		kb.addAxiom(top, makeSomeValues(role, atoms[0]));
		kb.addAxiom(atoms[0], atoms[1]);
		m_TableauMaster.init();
		AllValues all = makeAllValues(role, negatedAtoms[1]);
		assertFalse(m_TableauMaster.isSatisfiable(all, p));
	}

	@Test
	public void someValuesClash() {
		kb.addAxiom(atoms[0], makeSomeValues(role, atoms[1]));
		kb.addAxiom(makeSomeValues(role, top), negatedAtoms[0]);
		m_TableauMaster.init();
		assertFalse(m_TableauMaster.isSatisfiable(atoms[0], p));
	}
	
	@Test
	public void reopenParentAllValuesOnRestore() {
		m_TableauMaster.init();
		And bottom = makeAnd(atoms[0], negatedAtoms[0]);
		SomeValues some = makeSomeValues(role, makeOr(bottom, atoms[0]));
		AllValues all = makeAllValues(role, bottom);
		And wrapAll = makeAnd(atoms[2], makeAnd(atoms[1], all));
		And and = makeAnd(some, wrapAll);
		assertFalse(m_TableauMaster.isSatisfiable(and, p));
	}
	
}

