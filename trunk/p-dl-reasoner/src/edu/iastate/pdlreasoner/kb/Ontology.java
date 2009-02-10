package edu.iastate.pdlreasoner.kb;

import java.util.Arrays;
import java.util.List;


public class Ontology {

	//Constants
	private List<OntologyPackage> m_Packages;
	
	//Caches
	private ImportGraph m_ImportGraph;
	
	public Ontology(List<OntologyPackage> ontologyPackages) {
		m_Packages = ontologyPackages;
		
		for (OntologyPackage pack : ontologyPackages) {
			pack.init();
		}
		
		m_ImportGraph = new ImportGraph(m_Packages);
	}
	
	public Ontology(OntologyPackage... packages) {
		this(Arrays.asList(packages));
	}

	public List<OntologyPackage> getPackages() {
		return m_Packages;
	}
	
	public ImportGraph getImportGraph() {
		return m_ImportGraph;
	}
	
}
