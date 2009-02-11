package edu.iastate.pdlreasoner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.iastate.pdlreasoner.acceptance.TableauMasterMultiPackagePaperExampleTest;
import edu.iastate.pdlreasoner.acceptance.TableauMasterMultiPackageTest;
import edu.iastate.pdlreasoner.acceptance.TableauMasterSinglePackageTest;
import edu.iastate.pdlreasoner.kb.TBoxTest;
import edu.iastate.pdlreasoner.model.visitor.NNFConverterTest;
import edu.iastate.pdlreasoner.struct.RingTest;
import edu.iastate.pdlreasoner.tableau.NodeTest;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSetTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TBoxTest.class,
	NNFConverterTest.class,
	TableauMasterSinglePackageTest.class,
	TableauMasterMultiPackageTest.class,
	TableauMasterMultiPackagePaperExampleTest.class,
	RingTest.class,
	BranchPointSetTest.class,
	NodeTest.class	
})
public class AllTests {}
