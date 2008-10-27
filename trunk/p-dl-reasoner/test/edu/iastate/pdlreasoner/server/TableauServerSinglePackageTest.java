package edu.iastate.pdlreasoner.server;

import static edu.iastate.pdlreasoner.model.ModelFactory.makeAtom;
import static edu.iastate.pdlreasoner.model.ModelFactory.makePackage;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeTop;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import edu.iastate.pdlreasoner.kb.KnowledgeBase;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.Top;

public class TableauServerSinglePackageTest {

	private TableauServer m_TableauServer;
	private DLPackage p;
	private KnowledgeBase kb;
	private Top top;
	private Atom[] atoms;
	
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
	}

	@Test
	public void empty() {
		m_TableauServer.init();
		assertTrue(m_TableauServer.isConsistent(p));
	}

	@Test
	public void inconsistency() {
		kb.addAxiom(top, Bottom.INSTANCE);
		m_TableauServer.init();
		assertFalse(m_TableauServer.isConsistent(p));
	}

}

