package edu.iastate.pdlreasoner.kb;

import static edu.iastate.pdlreasoner.model.ModelFactory.makeAnd;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeAtom;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeNegation;
import static edu.iastate.pdlreasoner.model.ModelFactory.makePackage;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeSub;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeTop;
import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Top;
import edu.iastate.pdlreasoner.struct.MultiValuedMap;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TBoxTest {

	private TBox m_TBox;
	private DLPackage m_HomePackage;
	
	@Before
	public void setUp() {
		m_HomePackage = makePackage(URI.create("#package0"));
		KnowledgeBase kb = new KnowledgeBase(m_HomePackage);
		m_TBox = new TBox(kb);
	}
	
	@Test
	public void testGetExternalConcepts() {
		DLPackage ex1 = makePackage(URI.create("#package1"));
		DLPackage ex2 = makePackage(URI.create("#package2"));
		DLPackage ex3 = makePackage(URI.create("#package3"));
		Top topHome = makeTop(m_HomePackage);
		Atom atomHome = makeAtom(m_HomePackage, URI.create("#atom"));
		Top top1 = makeTop(ex1);
		Atom atom1 = makeAtom(ex1, URI.create("#atom"));
		Negation not3 = makeNegation(ex3, atom1);
		Top top2 = makeTop(ex2);
		And and2 = makeAnd(top2, Bottom.INSTANCE);
		
		m_TBox.addAxiom(makeSub(atomHome, topHome));
		m_TBox.addAxiom(makeSub(top1, atom1));
		m_TBox.addAxiom(makeSub(not3, and2));
		
		MultiValuedMap<DLPackage, Concept> expected = CollectionUtil.makeMultiValuedMap();
		expected.add(ex1, top1);
		expected.add(ex1, atom1);
		expected.add(ex2, top2);
		assertEquals(expected, m_TBox.getExternalConcepts());
	}

}
