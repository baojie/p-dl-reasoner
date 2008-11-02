package edu.iastate.pdlreasoner.tableau;

import static edu.iastate.pdlreasoner.model.ModelFactory.makeAllValues;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeAtom;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeNegation;
import static edu.iastate.pdlreasoner.model.ModelFactory.makePackage;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeRole;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import edu.iastate.pdlreasoner.model.AllValues;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Role;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class NodeTest {
	
	private Node m_Node;
	private DLPackage m_HomePackage;
	private DLPackage m_ForeignPackage;
	private Role role;
	private Atom atom;
	private AllValues all;
	
	@Before
	public void setUp() throws Exception {
		m_HomePackage = makePackage(URI.create("#package"));
		m_ForeignPackage = makePackage(URI.create("#package2"));
		TableauGraph g = new TableauGraph(m_HomePackage);
		m_Node = g.makeRoot();
		role = makeRole(URI.create("#role"));
		atom = makeAtom(m_HomePackage, URI.create("#atom"));
		all = makeAllValues(role, atom);
	}

	@Test
	public void testAddChildWith() {
		assertFalse(m_Node.containsChild(role, atom));
		assertEquals(Collections.EMPTY_SET, m_Node.getChildrenWith(role));
		Node child = m_Node.addChildBy(role, atom);
		assertTrue(m_Node.containsChild(role, atom));
		assertFalse(m_Node.containsChild(role, all));
		assertEquals(Collections.singleton(child), m_Node.getChildrenWith(role));
	}

	@Test
	public void testAddLabel() {
		assertFalse(m_Node.containsLabel(atom));
		assertTrue(m_Node.addLabel(atom));
		assertTrue(m_Node.containsLabel(atom));
	}

	@Test
	public void testFlushOpenLabels() {
		m_Node.addLabel(atom);
		m_Node.addLabel(all);
		
		assertFalse(m_Node.isComplete());
		Set<Concept> flushed = m_Node.flushOpenLabels();
		assertEquals(CollectionUtil.asSet(atom, all), flushed);
		assertTrue(m_Node.isComplete());
		assertEquals(Collections.singleton(all), m_Node.getExpandedAllValuesWith(role));
		assertTrue(m_Node.containsLabel(atom));
		assertTrue(m_Node.containsLabel(all));
		assertFalse(m_Node.addLabel(atom));
	}

	@Test
	public void testIsComplete() {
		assertTrue(m_Node.isComplete());
	}

	@Test
	public void testHasClash_Bottom() {
		assertFalse(m_Node.getClashCauses());
		m_Node.addLabel(Bottom.INSTANCE);
		assertTrue(m_Node.getClashCauses());
	}

	@Test
	public void testHasClash_Atom1() {
		Negation homeNotAtom = makeNegation(m_HomePackage, atom);
		Negation foreignNotAtom = makeNegation(m_ForeignPackage, atom);
		
		m_Node.addLabel(atom);
		m_Node.addLabel(foreignNotAtom);
		assertFalse(m_Node.getClashCauses());
		
		m_Node.addLabel(homeNotAtom);
		assertTrue(m_Node.getClashCauses());
	}

	@Test
	public void testHasClash_Atom2() {
		Negation homeNotAtom = makeNegation(m_HomePackage, atom);
		m_Node.addLabel(homeNotAtom);
		m_Node.addLabel(atom);
		assertTrue(m_Node.getClashCauses());
	}

	@Test
	public void testHasClash_All() {
		Negation homeNotAll = makeNegation(m_HomePackage, all);
		m_Node.addLabel(homeNotAll);
		m_Node.addLabel(all);
		//Only NNF support
		assertFalse(m_Node.getClashCauses());
	}
}
