package edu.iastate.pdlreasoner.kb;

import static edu.iastate.pdlreasoner.model.ModelFactory.makeAnd;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeAtom;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeNegation;
import static edu.iastate.pdlreasoner.model.ModelFactory.makePackageID;
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
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.model.Top;
import edu.iastate.pdlreasoner.struct.MultiValuedMap;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TBoxTest {

	private TBox m_TBox;
	private PackageID m_HomePackageID;
	
	@Before
	public void setUp() {
		m_HomePackageID = makePackageID(URI.create("#package0"));
		OntologyPackage pack = new OntologyPackage(m_HomePackageID);
		m_TBox = new TBox(pack);
	}
	
	@Test
	public void testGetExternalConcepts() {
		PackageID ex1 = makePackageID(URI.create("#package1"));
		PackageID ex2 = makePackageID(URI.create("#package2"));
		PackageID ex3 = makePackageID(URI.create("#package3"));
		Top topHome = makeTop(m_HomePackageID);
		Atom atomHome = makeAtom(m_HomePackageID, "atom");
		Top top1 = makeTop(ex1);
		Atom atom1 = makeAtom(ex1, "atom");
		Negation not3 = makeNegation(ex3, atom1);
		Top top2 = makeTop(ex2);
		And and2 = makeAnd(top2, Bottom.INSTANCE);
		
		m_TBox.addAxiom(makeSub(atomHome, topHome));
		m_TBox.addAxiom(makeSub(top1, atom1));
		m_TBox.addAxiom(makeSub(not3, and2));
		
		MultiValuedMap<PackageID, Concept> expected = CollectionUtil.makeMultiValuedMap();
		expected.add(ex1, top1);
		expected.add(ex1, atom1);
		expected.add(ex2, top2);
		assertEquals(expected, m_TBox.getExternalConcepts());
	}

}
