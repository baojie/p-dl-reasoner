package edu.iastate.pdlreasoner.kb.owlapi;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import edu.iastate.pdlreasoner.model.ModelFactory;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class OntologyPackageIDStore {

	private Map<URI,PackageID> m_Packages;
	
	public OntologyPackageIDStore() {
		m_Packages = CollectionUtil.makeMap();
	}
	
	public void addPackageID(URI uri) {
		URI filteredURI = null;
		try {
			filteredURI = new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		if (filteredURI == null || m_Packages.containsKey(filteredURI)) return;
		
		PackageID packageID = ModelFactory.makePackageID(filteredURI);
		m_Packages.put(filteredURI, packageID);		
	}
	
	public PackageID getPackageID(URI uri) {
		return m_Packages.get(uri);
	}
	
}
