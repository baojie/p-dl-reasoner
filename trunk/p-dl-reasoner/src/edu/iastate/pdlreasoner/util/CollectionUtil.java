package edu.iastate.pdlreasoner.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionUtil {

	public static <T> List<T> makeList() {
		return new ArrayList<T>();
	}
	
	public static <T> Set<T> makeSet() {
		return new HashSet<T>();
	}
	
	public static <K,V> Map<K,V> makeMap() {
		return new HashMap<K,V>();
	}
	
	public static <T> Set<T> asSet(T... as) {
		Set<T> set = new HashSet<T>(as.length);
		for (T a : as) {
			set.add(a);
		}
		return set;
	}
	
	public static <T> Set<T> copy(Set<? extends T> a) {
		return new HashSet<T>(a);
	}
	
	public static <T> Set<T> emptySetIfNull(Set<T> a) {
		return (a == null) ? Collections.<T>emptySet() : a;
	}

}