package edu.iastate.pdlreasoner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.iastate.pdlreasoner.kb.TBoxTest;
import edu.iastate.pdlreasoner.model.visitor.NNFConverterTest;
import edu.iastate.pdlreasoner.server.TableauServerMultiPackageTest;
import edu.iastate.pdlreasoner.server.TableauServerSinglePackageTest;
import edu.iastate.pdlreasoner.struct.RingTest;
import edu.iastate.pdlreasoner.tableau.NodeTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TBoxTest.class,
	NNFConverterTest.class,
	TableauServerSinglePackageTest.class,
	TableauServerMultiPackageTest.class,
	RingTest.class,
	NodeTest.class	
})
public class AllTests {}
