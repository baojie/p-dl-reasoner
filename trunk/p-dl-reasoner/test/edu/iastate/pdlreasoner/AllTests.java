package edu.iastate.pdlreasoner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.iastate.pdlreasoner.kb.TBoxTest;
import edu.iastate.pdlreasoner.master.TableauServerMultiPackagePaperExampleTest;
import edu.iastate.pdlreasoner.master.TableauServerMultiPackageTest;
import edu.iastate.pdlreasoner.master.TableauServerSinglePackageTest;
import edu.iastate.pdlreasoner.model.visitor.NNFConverterTest;
import edu.iastate.pdlreasoner.struct.RingTest;
import edu.iastate.pdlreasoner.tableau.NodeTest;
import edu.iastate.pdlreasoner.tableau.branch.BranchPointSetTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TBoxTest.class,
	NNFConverterTest.class,
	TableauServerSinglePackageTest.class,
	TableauServerMultiPackageTest.class,
	TableauServerMultiPackagePaperExampleTest.class,
	RingTest.class,
	BranchPointSetTest.class,
	NodeTest.class	
})
public class AllTests {}
