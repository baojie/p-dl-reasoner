package edu.iastate.pdlreasoner.kb;

import java.util.Arrays;
import java.util.List;


public class Ontology {

	//Constants
	private List<OntologyPackage> m_Packages;
	
	//Caches
	private ImportGraph m_ImportGraph;
	
	public Ontology(OntologyPackage[] packages) {
		m_Packages = Arrays.asList(packages);
		
		for (OntologyPackage pack : packages) {
			pack.init();
		}
		
		m_ImportGraph = new ImportGraph(m_Packages);
	}
	
	public List<OntologyPackage> getPackages() {
		return m_Packages;
	}
	
	public ImportGraph getImportGraph() {
		return m_ImportGraph;
	}
	
}
