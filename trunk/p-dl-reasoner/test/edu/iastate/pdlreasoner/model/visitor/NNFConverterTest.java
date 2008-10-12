package edu.iastate.pdlreasoner.model.visitor;

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
import edu.iastate.pdlreasoner.model.visitor.NNFConverter;

public class NNFConverterTest {
	
	private DLPackage[] p;
	private Top[] top;
	private Atom[] atoms;
	private Negation[] negatedAtoms;
	private Role[] roles;
	private Or or;
	private And and;
	private SomeValues someR0A0;
	private AllValues allR0A0;
	
	@Before
	public void setUp() {
		p = new DLPackage[3];
		for (int i = 0; i < p.length; i++) {
			p[i] = makePackage(URI.create("#package" + i));
		}
		top = new Top[p.length];
		for (int i = 0; i < top.length; i++) {
			top[i] = makeTop(p[i]);
		}
		atoms = new Atom[5];
		for (int i = 0; i < atoms.length; i++) {
			atoms[i] = makeAtom(p[0], URI.create("#atom" + i));
		}
		negatedAtoms = new Negation[atoms.length];
		for (int i = 0; i < negatedAtoms.length; i++) {
			negatedAtoms[i] = makeNegation(p[0], atoms[i]);
		}
		
		roles = new Role[5];
		for (int i = 0; i < roles.length; i++) {
			roles[i] = makeRole(URI.create("#role" + i));
		}
		or = makeOr(atoms);
		and = makeAnd(atoms);
		someR0A0 = makeSomeValues(roles[0], atoms[0]);
		allR0A0 = makeAllValues(roles[0], atoms[0]);
	}
	
	@Test
	public void none() {
		NNFConverter converter = new NNFConverter(p[0]);
		
		assertEquals(top[0], converter.convert(top[0]));
		assertEquals(atoms[0], converter.convert(atoms[0]));
		assertEquals(or, converter.convert(or));
		assertEquals(and, converter.convert(and));
		assertEquals(someR0A0, converter.convert(someR0A0));
		assertEquals(allR0A0, converter.convert(allR0A0));
		
		Negation notA0 = makeNegation(p[0], atoms[0]);
		assertEquals(notA0, converter.convert(notA0));
	}

	@Test
	public void local() {
		NNFConverter converter = new NNFConverter(p[0]);
		
		assertEquals(Bottom.INSTANCE, converter.convert(makeNegation(p[0], top[0])));
		assertEquals(makeAnd(negatedAtoms), converter.convert(makeNegation(p[0], or)));
		assertEquals(makeOr(negatedAtoms), converter.convert(makeNegation(p[0], and)));
		
		AllValues allNotA0 = makeAllValues(roles[0], negatedAtoms[0]);
		assertEquals(allNotA0, converter.convert(makeNegation(p[0], someR0A0)));
		SomeValues someNotA0 = makeSomeValues(roles[0], negatedAtoms[0]);
		assertEquals(someNotA0, converter.convert(makeNegation(p[0], allR0A0)));
		
		assertEquals(atoms[0], converter.convert(makeNegation(p[0], negatedAtoms[0])));
	}

	@Test
	public void foreign() {
		NNFConverter converter = new NNFConverter(p[0]);
		Concept expected = null;
		Concept actual = null;
		
		expected = makeTop(p[1]);
		actual = converter.convert(makeNegation(p[1], Bottom.INSTANCE));
		assertEquals(expected, actual);
		
		expected = Bottom.INSTANCE;
		actual = converter.convert(makeNegation(p[1], makeTop(p[1])));
		assertEquals(expected, actual);
		
		expected = makeAnd(makeTop(p[1]), makeNegation(p[0], atoms[0]));
		actual = converter.convert(makeNegation(p[1], atoms[0]));
		assertEquals(expected, actual);

		expected = makeAnd(makeTop(p[2]), makeOr(atoms[0], makeNegation(p[2], makeTop(p[1]))));
		actual = converter.convert(makeNegation(p[2], makeNegation(p[1], atoms[0])));
		assertEquals(expected, actual);
		
		Concept nnfNegatedA0 = converter.convert(makeNegation(p[1], atoms[0]));
		Concept nnfNegatedA1 = converter.convert(makeNegation(p[1], atoms[1]));
		
		expected = makeOr(nnfNegatedA0, nnfNegatedA1);
		actual = converter.convert(makeNegation(p[1], makeAnd(atoms[0], atoms[1])));
		assertEquals(expected, actual);

		expected = makeAnd(nnfNegatedA0, nnfNegatedA1);
		actual = converter.convert(makeNegation(p[1], makeOr(atoms[0], atoms[1])));
		assertEquals(expected, actual);

		expected = makeAnd(makeTop(p[1]), makeAllValues(roles[0], negatedAtoms[0]));
		actual = converter.convert(makeNegation(p[1], makeSomeValues(roles[0], atoms[0])));
		assertEquals(expected, actual);

		expected = makeAnd(makeTop(p[1]), makeSomeValues(roles[0], negatedAtoms[0]));
		actual = converter.convert(makeNegation(p[1], makeAllValues(roles[0], atoms[0])));
		assertEquals(expected, actual);
	}

}
