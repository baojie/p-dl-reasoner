package edu.iastate.pdlreasoner.master;

import static edu.iastate.pdlreasoner.model.ModelFactory.makeAtom;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeOr;
import static edu.iastate.pdlreasoner.model.ModelFactory.makePackage;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import edu.iastate.pdlreasoner.exception.IllegalQueryException;
import edu.iastate.pdlreasoner.kb.KnowledgeBase;
import edu.iastate.pdlreasoner.master.TableauServer;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.Or;

public class TableauServerMultiPackageTest {

	private DLPackage[] p;
	private KnowledgeBase[] kb;
	private TableauServer m_TableauServer;
	
	@Before
	public void setUp() {
		p = new DLPackage[3];
		for (int i = 0; i < p.length; i++) {
			p[i] = makePackage(URI.create("#package" + i));
		}
		
		kb = new KnowledgeBase[p.length];
		for (int i = 0; i < kb.length; i++) {
			kb[i] = new KnowledgeBase(p[i]);
		}
		
		m_TableauServer = new TableauServer();
	}

	@Test
	public void empty() {
		m_TableauServer.addKnowledgeBase(kb[0]);
		m_TableauServer.addKnowledgeBase(kb[1]);
		m_TableauServer.init();
		assertTrue(m_TableauServer.isConsistent(p[0]));
		assertTrue(m_TableauServer.isConsistent(p[1]));
	}

	@Test
	public void understandability() {
		Atom p0A = makeAtom(p[0], URI.create("#A"));
		Atom p0B = makeAtom(p[0], URI.create("#B"));
		
		kb[0].addAxiom(p0A, Bottom.INSTANCE);
		
		for (int i = 0; i <= 1; i++) {
			m_TableauServer.addKnowledgeBase(kb[i]);
		}
		
		m_TableauServer.init();
		Or AorB = makeOr(p0A, p0B);
		
		try {
			m_TableauServer.isSatisfiable(AorB, p[1]);
			fail("Expected IllegalQueryException");
		} catch (IllegalQueryException ex) {
		}
	}

	@Test
	public void pruneInterTableauxOnClash() {
		Atom p0A = makeAtom(p[0], URI.create("#A"));
		Atom p0B = makeAtom(p[0], URI.create("#B"));
		
		kb[0].addAxiom(p0A, Bottom.INSTANCE);
		kb[0].addAxiom(p0B, Bottom.INSTANCE);
		kb[1].addAxiom(Bottom.INSTANCE, p0A);
		
		for (int i = 0; i <= 1; i++) {
			m_TableauServer.addKnowledgeBase(kb[i]);
		}
		
		m_TableauServer.init();
		Or query = makeOr(p0A, p0B);
		assertFalse(m_TableauServer.isSatisfiable(query, p[0]));
		assertFalse(m_TableauServer.isSatisfiable(query, p[1]));
	}

}