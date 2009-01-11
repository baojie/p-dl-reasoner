package edu.iastate.pdlreasoner.master;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.iastate.pdlreasoner.kb.KnowledgeBase;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.struct.Ring;
import edu.iastate.pdlreasoner.tableau.TableauManager;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TableauTopology implements Iterable<TableauManager> {

	private Map<DLPackage, TableauManager> m_Tableaux;
	private Ring<TableauManager> m_TableauxRing;

	public TableauTopology(List<KnowledgeBase> kbs) {
		m_Tableaux = CollectionUtil.makeMap();
		for (KnowledgeBase kb : kbs) {
			m_Tableaux.put(kb.getPackage(), kb.getTableau());
		}

		m_TableauxRing = new Ring<TableauManager>(m_Tableaux.values());
	}
	
	public TableauManager get(DLPackage dlPackage) {
		return m_Tableaux.get(dlPackage);
	}

	public TableauManager getNext(TableauManager man) {
		return m_TableauxRing.getNext(man);
	}

	@Override
	public Iterator<TableauManager> iterator() {
		return m_Tableaux.values().iterator();
	}
}
