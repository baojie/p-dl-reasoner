package edu.iastate.pdlreasoner.tableau;

import static edu.iastate.pdlreasoner.model.ModelFactory.makeAtom;
import static edu.iastate.pdlreasoner.model.ModelFactory.makePackage;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeRole;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.Role;

public class NodeTest {
	
	private Node m_Node;
	private Role role;
	private Atom atom;

	@Before
	public void setUp() throws Exception {
		DLPackage homePackage = makePackage(URI.create("#package"));
		TableauGraph g = new TableauGraph(homePackage);
		m_Node = g.makeRoot();
		role = makeRole(URI.create("#role"));
		atom = makeAtom(homePackage, URI.create("#atom"));
	}

	@Test
	public void testAddChildWith() {
		assertFalse(m_Node.containsChild(role, atom));
		m_Node.addChildWith(role, atom);
		assertTrue(m_Node.containsChild(role, atom));
	}

	@Test
	public void testAddLabel() {
		assertFalse(m_Node.containsLabel(atom));
		m_Node.addLabel(atom);
		assertTrue(m_Node.containsLabel(atom));
	}

	@Test
	public void testFlushOpenLabels() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsComplete() {
		fail("Not yet implemented");
	}

	@Test
	public void testHasClash() {
		fail("Not yet implemented");
	}

}
