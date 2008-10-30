package edu.iastate.pdlreasoner.tableau;

import edu.iastate.pdlreasoner.model.DLPackage;

public class BranchPoint {
	
	public static final BranchPoint ORIGIN = new BranchPoint();

	private int m_Time;
	private DLPackage m_Package;
	private int m_BranchIndex;

}
