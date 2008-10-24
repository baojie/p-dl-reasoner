package edu.iastate.pdlreasoner.server;

import static edu.iastate.pdlreasoner.model.ModelFactory.makePackage;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import edu.iastate.pdlreasoner.kb.KnowledgeBase;
import edu.iastate.pdlreasoner.model.DLPackage;

public class TableauServerSinglePackageTest {

	private DLPackage p;
	private KnowledgeBase kb;
	private TableauServer m_TableauServer;
	
	@Before
	public void setUp() {
		p = makePackage(URI.create("#package"));
		kb = new KnowledgeBase(p);
		m_TableauServer = new TableauServer();
	}

	@Test
	public void empty() {
		m_TableauServer.addKnowledgeBase(kb);
		m_TableauServer.init();
		assertTrue(m_TableauServer.isConsistent(p));
	}
	
}

