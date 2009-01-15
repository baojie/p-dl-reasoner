package edu.iastate.pdlreasoner.struct;

import java.util.Map;

import edu.iastate.pdlreasoner.util.CollectionUtil;

public class CounterMap<K> {

	private Map<K,IntCounter> m_Counters;
	
	public CounterMap() {
		m_Counters = CollectionUtil.makeMap();
	}
	
	public int next(K key) {
		IntCounter counter = m_Counters.get(key);
		if (counter == null) {
			counter = new IntCounter();
			m_Counters.put(key, counter);
		}
		
		return counter.next();
	}
	
	private static class IntCounter {
		
		private int m_Next;
		
		public int next() {
			return m_Next++;
		}
		
	}
	
}
