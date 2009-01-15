package edu.iastate.pdlreasoner.master;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jgroups.Address;

import edu.iastate.pdlreasoner.kb.OntologyPackage;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.struct.Ring;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TableauTopology implements Iterable<Address> {

	private Map<PackageID, Address> m_Tableaux;
	private Ring<Address> m_TableauxRing;

	public TableauTopology(List<OntologyPackage> packages, List<Address> addresses) {
		m_Tableaux = CollectionUtil.makeMap();
		for (int i = 0; i < packages.size(); i++) {
			m_Tableaux.put(packages.get(i).getID(), addresses.get(i));
		}

		m_TableauxRing = new Ring<Address>(m_Tableaux.values());
	}
	
	public Address get(PackageID packageID) {
		return m_Tableaux.get(packageID);
	}

	public Address getNext(Address a) {
		return m_TableauxRing.getNext(a);
	}

	@Override
	public Iterator<Address> iterator() {
		return m_Tableaux.values().iterator();
	}
	
	public Set<Entry<PackageID, Address>> entrySet() {
		return m_Tableaux.entrySet();
	}
	
}