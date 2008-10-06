package edu.iastate.pdlreasoner.util;

import static org.junit.Assert.assertEquals;
import static edu.iastate.pdlreasoner.model.ModelFactory.*;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import edu.iastate.pdlreasoner.model.AllValues;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.Role;
import edu.iastate.pdlreasoner.model.SomeValues;
import edu.iastate.pdlreasoner.model.Top;

public class NNFConverterTest {
	private DLPackage m_PA;
	private Top m_PATop;
	private Atom[] m_Atoms;
	private Negation[] m_NegatedAtoms;
	private Role[] m_Roles;
	private Or m_Or;
	private And m_And;
	private SomeValues m_SomeR0A0;
	private AllValues m_AllR0A0;
	
	@Before
	public void setUp() {
		m_PA = makePackage(URI.create("#packageA"));
		m_PATop = makeTop(m_PA);
		m_Atoms = new Atom[5];
		for (int i = 0; i < m_Atoms.length; i++) {
			m_Atoms[i] = makeAtom(m_PA, URI.create("#atom" + i));
		}
		m_NegatedAtoms = new Negation[m_Atoms.length];
		for (int i = 0; i < m_NegatedAtoms.length; i++) {
			m_NegatedAtoms[i] = makeNegation(m_PA, m_Atoms[i]);
		}
		
		m_Roles = new Role[5];
		for (int i = 0; i < m_Roles.length; i++) {
			m_Roles[i] = makeRole(URI.create("#role" + i));
		}
		m_Or = makeOr(m_Atoms);
		m_And = makeAnd(m_Atoms);
		m_SomeR0A0 = makeSomeValues(m_Roles[0], m_Atoms[0]);
		m_AllR0A0 = makeAllValues(m_Roles[0], m_Atoms[0]);
	}
	
	@Test
	public void none() {
		NNFConverter converter = new NNFConverter(m_PA);
		
		assertEquals(m_PATop, converter.convert(m_PATop));
		assertEquals(m_Atoms[0], converter.convert(m_Atoms[0]));
		assertEquals(m_Or, converter.convert(m_Or));
		assertEquals(m_And, converter.convert(m_And));
		assertEquals(m_SomeR0A0, converter.convert(m_SomeR0A0));
		assertEquals(m_AllR0A0, converter.convert(m_AllR0A0));
		
		Negation notA0 = makeNegation(m_PA, m_Atoms[0]);
		assertEquals(notA0, converter.convert(notA0));
	}

	@Test
	public void local() {
		NNFConverter converter = new NNFConverter(m_PA);
		
		assertEquals(Bottom.INSTANCE, converter.convert(makeNegation(m_PA, m_PATop)));
		assertEquals(makeAnd(m_NegatedAtoms), converter.convert(makeNegation(m_PA, m_Or)));
		assertEquals(makeOr(m_NegatedAtoms), converter.convert(makeNegation(m_PA, m_And)));
		
		AllValues allNotA0 = makeAllValues(m_Roles[0], m_NegatedAtoms[0]);
		assertEquals(allNotA0, converter.convert(makeNegation(m_PA, m_SomeR0A0)));
		SomeValues someNotA0 = makeSomeValues(m_Roles[0], m_NegatedAtoms[0]);
		assertEquals(someNotA0, converter.convert(makeNegation(m_PA, m_AllR0A0)));
		
		assertEquals(m_Atoms[0], converter.convert(makeNegation(m_PA, m_NegatedAtoms[0])));
	}
}
