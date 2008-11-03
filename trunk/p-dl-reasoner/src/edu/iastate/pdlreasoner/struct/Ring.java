package edu.iastate.pdlreasoner.struct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ring<E> {
	
	private Map<E, E> m_Ring;

	public Ring(Collection<E> col) {
		if (col.isEmpty()) return;
		
		m_Ring = new HashMap<E, E>();
		
		List<E> list = new ArrayList<E>(col);
		for (int i = 0; i < list.size() - 1; i++) {
			m_Ring.put(list.get(i), list.get(i + 1));
		}
		m_Ring.put(list.get(list.size() - 1), list.get(0));
	}
	
	public E getNext(E e) {
		return m_Ring.get(e);
	}
	
}
