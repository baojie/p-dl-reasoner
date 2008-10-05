package edu.iastate.pdlreasoner.util;

import static org.junit.Assert.*;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.ModelFactory;

public class NNFConverterTest {
	private ModelFactory m_Factory;
	private DLPackage m_PA;
	private Atom[] m_Atoms;
	
	@Before
	public void setUp() {
		m_Factory = new ModelFactory();
		m_PA = m_Factory.makePackage(URI.create("#packageA"));
		m_Atoms = new Atom[5];
		for (int i = 0; i < m_Atoms.length; i++) {
			m_Atoms[i] = m_Factory.makeAtom(m_PA, URI.create("#atom" + i));
		}		
	}
	
	@Test
	public void none() {
		assertEquals(m_Atoms[0], NNFConverter.convert(m_Atoms[0]));
	}
}
