package edu.iastate.pdlreasoner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.iastate.pdlreasoner.kb.TBoxTest;
import edu.iastate.pdlreasoner.model.visitor.NNFConverterTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	NNFConverterTest.class,
	TBoxTest.class
})
public class AllTests {}
