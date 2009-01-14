package edu.iastate.pdlreasoner.master;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.iastate.pdlreasoner.kb.OntologyPackage;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.struct.Ring;
import edu.iastate.pdlreasoner.tableau.TableauManagerOld;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TableauTopology implements Iterable<TableauManagerOld> {

	private Map<PackageID, TableauManagerOld> m_Tableaux;
	private Ring<TableauManagerOld> m_TableauxRing;

	public TableauTopology(List<OntologyPackage> packages) {
		m_Tableaux = CollectionUtil.makeMap();
		for (OntologyPackage pack : packages) {
			m_Tableaux.put(pack.getID(), pack.getTableau());
		}

		m_TableauxRing = new Ring<TableauManagerOld>(m_Tableaux.values());
	}
	
	public TableauManagerOld get(PackageID packageID) {
		return m_Tableaux.get(packageID);
	}

	public TableauManagerOld getNext(TableauManagerOld man) {
		return m_TableauxRing.getNext(man);
	}

	@Override
	public Iterator<TableauManagerOld> iterator() {
		return m_Tableaux.values().iterator();
	}
}
