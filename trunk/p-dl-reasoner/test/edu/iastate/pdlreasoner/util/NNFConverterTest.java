package edu.iastate.pdlreasoner.util;

import static edu.iastate.pdlreasoner.model.ModelFactory.makeAllValues;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeAnd;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeAtom;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeNegation;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeOr;
import static edu.iastate.pdlreasoner.model.ModelFactory.makePackage;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeRole;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeSomeValues;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeTop;
import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import edu.iastate.pdlreasoner.model.AllValues;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.Role;
import edu.iastate.pdlreasoner.model.SomeValues;
import edu.iastate.pdlreasoner.model.Top;

public class NNFConverterTest {
	private DLPackage[] m_P;
	private Top[] m_Top;
	private Atom[] m_Atoms;
	private Negation[] m_NegatedAtoms;
	private Role[] m_Roles;
	private Or m_Or;
	private And m_And;
	private SomeValues m_SomeR0A0;
	private AllValues m_AllR0A0;
	
	@Before
	public void setUp() {
		m_P = new DLPackage[3];
		for (int i = 0; i < m_P.length; i++) {
			m_P[i] = makePackage(URI.create("#package" + i));
		}
		m_Top = new Top[m_P.length];
		for (int i = 0; i < m_Top.length; i++) {
			m_Top[i] = makeTop(m_P[i]);
		}
		m_Atoms = new Atom[5];
		for (int i = 0; i < m_Atoms.length; i++) {
			m_Atoms[i] = makeAtom(m_P[0], URI.create("#atom" + i));
		}
		m_NegatedAtoms = new Negation[m_Atoms.length];
		for (int i = 0; i < m_NegatedAtoms.length; i++) {
			m_NegatedAtoms[i] = makeNegation(m_P[0], m_Atoms[i]);
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
		NNFConverter converter = new NNFConverter(m_P[0]);
		
		assertEquals(m_Top[0], converter.convert(m_Top[0]));
		assertEquals(m_Atoms[0], converter.convert(m_Atoms[0]));
		assertEquals(m_Or, converter.convert(m_Or));
		assertEquals(m_And, converter.convert(m_And));
		assertEquals(m_SomeR0A0, converter.convert(m_SomeR0A0));
		assertEquals(m_AllR0A0, converter.convert(m_AllR0A0));
		
		Negation notA0 = makeNegation(m_P[0], m_Atoms[0]);
		assertEquals(notA0, converter.convert(notA0));
	}

	@Test
	public void local() {
		NNFConverter converter = new NNFConverter(m_P[0]);
		
		assertEquals(Bottom.INSTANCE, converter.convert(makeNegation(m_P[0], m_Top[0])));
		assertEquals(makeAnd(m_NegatedAtoms), converter.convert(makeNegation(m_P[0], m_Or)));
		assertEquals(makeOr(m_NegatedAtoms), converter.convert(makeNegation(m_P[0], m_And)));
		
		AllValues allNotA0 = makeAllValues(m_Roles[0], m_NegatedAtoms[0]);
		assertEquals(allNotA0, converter.convert(makeNegation(m_P[0], m_SomeR0A0)));
		SomeValues someNotA0 = makeSomeValues(m_Roles[0], m_NegatedAtoms[0]);
		assertEquals(someNotA0, converter.convert(makeNegation(m_P[0], m_AllR0A0)));
		
		assertEquals(m_Atoms[0], converter.convert(makeNegation(m_P[0], m_NegatedAtoms[0])));
	}

	@Test
	public void foreign() {
		NNFConverter converter = new NNFConverter(m_P[0]);
		Concept expected = null;
		Concept actual = null;
		
		expected = makeTop(m_P[1]);
		actual = converter.convert(makeNegation(m_P[1], Bottom.INSTANCE));
		assertEquals(expected, actual);
		
		expected = Bottom.INSTANCE;
		actual = converter.convert(makeNegation(m_P[1], makeTop(m_P[1])));
		assertEquals(expected, actual);
		
		expected = makeAnd(makeTop(m_P[1]), makeNegation(m_P[0], m_Atoms[0]));
		actual = converter.convert(makeNegation(m_P[1], m_Atoms[0]));
		assertEquals(expected, actual);

		expected = makeAnd(makeTop(m_P[2]), makeOr(m_Atoms[0], makeNegation(m_P[2], makeTop(m_P[1]))));
		actual = converter.convert(makeNegation(m_P[2], makeNegation(m_P[1], m_Atoms[0])));
		assertEquals(expected, actual);
		
		Concept nnfNegatedA0 = converter.convert(makeNegation(m_P[1], m_Atoms[0]));
		Concept nnfNegatedA1 = converter.convert(makeNegation(m_P[1], m_Atoms[1]));
		
		expected = makeOr(nnfNegatedA0, nnfNegatedA1);
		actual = converter.convert(makeNegation(m_P[1], makeAnd(m_Atoms[0], m_Atoms[1])));
		assertEquals(expected, actual);

		expected = makeAnd(nnfNegatedA0, nnfNegatedA1);
		actual = converter.convert(makeNegation(m_P[1], makeOr(m_Atoms[0], m_Atoms[1])));
		assertEquals(expected, actual);

		expected = makeAnd(makeTop(m_P[1]), makeAllValues(m_Roles[0], m_NegatedAtoms[0]));
		actual = converter.convert(makeNegation(m_P[1], makeSomeValues(m_Roles[0], m_Atoms[0])));
		assertEquals(expected, actual);

		expected = makeAnd(makeTop(m_P[1]), makeSomeValues(m_Roles[0], m_NegatedAtoms[0]));
		actual = converter.convert(makeNegation(m_P[1], makeAllValues(m_Roles[0], m_Atoms[0])));
		assertEquals(expected, actual);
	}

}
