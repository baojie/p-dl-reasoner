package edu.iastate.pdlreasoner.struct;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MultiValuedMap<K,V> extends HashMap<K,Set<V>> {

    private static final long serialVersionUID = 1L;

	public boolean add(K key, V value) {
        Set<V> values = super.get(key);
        if (values == null) {
            values = new HashSet<V>();
            super.put(key, values);
        }
        
        return values.add(value);
    }
    
    public boolean add(K key, Set<V> values) {
        Set<V> oldValues = super.get(key);
        if (oldValues == null) {
            oldValues = new HashSet<V>();
            super.put(key, oldValues);
        }
        
        return oldValues.addAll(values);
    }

    public Set<V> put(K key, V value) {
        Set<V> set = new HashSet<V>();
        set.add(value);
        
        return super.put(key, set);
    }

    public Set<V> put(K key, Set<V> values) {
        return super.put(key, values);
    }

    public boolean remove(K key, V value) {
    	boolean hasRemoved = false;
    	
        Set<V> values = super.get(key);
        if (values != null) { 
            hasRemoved = values.remove(value);
            
            if (values.isEmpty()) super.remove(key);
        }
        
        return hasRemoved;
    }

}