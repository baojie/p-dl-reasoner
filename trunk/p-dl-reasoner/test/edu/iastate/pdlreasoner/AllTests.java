package edu.iastate.pdlreasoner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.iastate.pdlreasoner.acceptance.MultiPackagePaperExampleTest;
import edu.iastate.pdlreasoner.acceptance.MultiPackageTest;
import edu.iastate.pdlreasoner.acceptance.SinglePackageTest;
import edu.iastate.pdlreasoner.kb.TBoxTest;
import edu.iastate.pdlreasoner.model.visitor.NNFConverterTest;
import edu.iastate.pdlreasoner.struct.RingTest;
import edu.iastate.pdlreasoner.tableau.NodeTest;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSetTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TBoxTest.class,
	NNFConverterTest.class,
	RingTest.class,
	BranchPointSetTest.class,
	NodeTest.class,	
	SinglePackageTest.class,
	MultiPackageTest.class,
	MultiPackagePaperExampleTest.class
})
public class AllTests {}
