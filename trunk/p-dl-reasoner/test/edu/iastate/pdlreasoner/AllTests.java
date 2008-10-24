package edu.iastate.pdlreasoner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.iastate.pdlreasoner.kb.TBoxTest;
import edu.iastate.pdlreasoner.model.visitor.NNFConverterTest;
import edu.iastate.pdlreasoner.server.TableauServerTest;
import edu.iastate.pdlreasoner.tableau.NodeTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	NodeTest.class,
	NNFConverterTest.class,
	TBoxTest.class,
	TableauServerTest.class
})
public class AllTests {}
