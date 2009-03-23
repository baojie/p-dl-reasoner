package edu.iastate.pdlreasoner.struct;

import java.util.HashMap;
import java.util.Map;

public class BiMap<A, B> {

	private Map<A, B> m_AB;
	private Map<B, A> m_BA;
	
	public BiMap() {
		m_AB = new HashMap<A,B>();
		m_BA = new HashMap<B,A>();
	}
	
	public void add(A a, B b) {
		m_AB.put(a, b);
		m_BA.put(b, a);
	}
	
	public A getA(B b) {
		return m_BA.get(b);
	}

	public B getB(A a) {
		return m_AB.get(a);
	}
	
	public int size() {
		return m_AB.size();
	}
	
}
