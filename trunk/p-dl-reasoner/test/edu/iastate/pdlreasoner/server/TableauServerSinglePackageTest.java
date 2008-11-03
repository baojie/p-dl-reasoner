package edu.iastate.pdlreasoner.server;

import static edu.iastate.pdlreasoner.model.ModelFactory.makeAnd;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeAtom;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeNegation;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeOr;
import static edu.iastate.pdlreasoner.model.ModelFactory.makePackage;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeTop;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import edu.iastate.pdlreasoner.kb.KnowledgeBase;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.Top;

public class TableauServerSinglePackageTest {

	private TableauServer m_TableauServer;
	private DLPackage p;
	private KnowledgeBase kb;
	private Top top;
	private Atom[] atoms;
	private Negation[] negatedAtoms;
	
	@Before
	public void setUp() {
		m_TableauServer = new TableauServer();
		p = makePackage(URI.create("#package"));
		kb = new KnowledgeBase(p);
		m_TableauServer.addKnowledgeBase(kb);
		top = makeTop(p);
		atoms = new Atom[5];
		for (int i = 0; i < atoms.length; i++) {
			atoms[i] = makeAtom(p, URI.create("#atom" + i));
		}
		negatedAtoms = new Negation[atoms.length];
		for (int i = 0; i < negatedAtoms.length; i++) {
			negatedAtoms[i] = makeNegation(p, atoms[i]);
		}
	}

	@Test
	public void empty() {
		m_TableauServer.init();
		assertTrue(m_TableauServer.isConsistent(p));
	}

	@Test
	public void bottom1() {
		kb.addAxiom(top, Bottom.INSTANCE);
		m_TableauServer.init();
		assertFalse(m_TableauServer.isConsistent(p));
	}
	
	@Test
	public void bottom2() {
		kb.addAxiom(top, atoms[0]);
		kb.addAxiom(atoms[0], Bottom.INSTANCE);
		m_TableauServer.init();
		assertFalse(m_TableauServer.isConsistent(p));
	}
	
	@Test
	public void and() {
		And bottom = makeAnd(atoms[1], negatedAtoms[1]);
		kb.addAxiom(atoms[0], bottom);
		m_TableauServer.init();
		assertFalse(m_TableauServer.isSatisfiable(atoms[0], p));
		assertTrue(m_TableauServer.isSatisfiable(atoms[1], p));
	}

	@Test
	public void andOr() {
		Or negatedAtom0or1 = makeOr(negatedAtoms[0], negatedAtoms[1]);
		kb.addAxiom(top, negatedAtom0or1);
		m_TableauServer.init();
		And atom0and1 = makeAnd(atoms[0], atoms[1]);
		assertFalse(m_TableauServer.isSatisfiable(atom0and1, p));
	}
	
	@Test
	public void nestedOr() {
		Or atom01or2 = makeOr(makeAnd(atoms[0], atoms[1]), atoms[2]);
		Or negatedAtom0or1 = makeOr(negatedAtoms[0], negatedAtoms[1]);
		kb.addAxiom(top, atom01or2);
		kb.addAxiom(top, negatedAtom0or1);
		m_TableauServer.init();
		assertTrue(m_TableauServer.isConsistent(p));
	}

	@Test
	public void subclassOf() {
		
	}

}

