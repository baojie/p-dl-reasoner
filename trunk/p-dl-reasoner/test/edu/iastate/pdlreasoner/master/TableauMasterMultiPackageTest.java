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
import edu.iastate.pdlreasoner.master.TableauMaster;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.Or;

public class TableauMasterMultiPackageTest {

	private DLPackage[] p;
	private KnowledgeBase[] kb;
	private TableauMaster m_TableauMaster;
	
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
		
		m_TableauMaster = new TableauMaster();
	}

	@Test
	public void empty() {
		m_TableauMaster.addKnowledgeBase(kb[0]);
		m_TableauMaster.addKnowledgeBase(kb[1]);
		m_TableauMaster.init();
		assertTrue(m_TableauMaster.isConsistent(p[0]));
		assertTrue(m_TableauMaster.isConsistent(p[1]));
	}

	@Test
	public void understandability() {
		Atom p0A = makeAtom(p[0], URI.create("#A"));
		Atom p0B = makeAtom(p[0], URI.create("#B"));
		
		kb[0].addAxiom(p0A, Bottom.INSTANCE);
		
		for (int i = 0; i <= 1; i++) {
			m_TableauMaster.addKnowledgeBase(kb[i]);
		}
		
		m_TableauMaster.init();
		Or AorB = makeOr(p0A, p0B);
		
		try {
			m_TableauMaster.isSatisfiable(AorB, p[1]);
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
			m_TableauMaster.addKnowledgeBase(kb[i]);
		}
		
		m_TableauMaster.init();
		Or query = makeOr(p0A, p0B);
		assertFalse(m_TableauMaster.isSatisfiable(query, p[0]));
		assertFalse(m_TableauMaster.isSatisfiable(query, p[1]));
	}

}