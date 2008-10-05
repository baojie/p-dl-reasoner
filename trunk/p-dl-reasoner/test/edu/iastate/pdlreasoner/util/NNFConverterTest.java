package edu.iastate.pdlreasoner.util;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.ModelFactory;

public class NNFConverterTest {
	private ModelFactory m_Factory;
	private DLPackage m_PA;
	
	@Before
	public void setUp() {
		m_Factory = new ModelFactory();
		m_PA = m_Factory.makePackage(URI.create("A.owl"));
		
	}
	
	@Test
	public void none() {
		
	}
}
