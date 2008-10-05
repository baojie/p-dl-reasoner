package edu.iastate.pdlreasoner.util;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;

public class NNFConverter {
	
	private DLPackage m_Context;
	
	public NNFConverter(DLPackage context) {
		m_Context = context;
	}

	public Concept convert(Concept c) {
		return c;
	}
}
