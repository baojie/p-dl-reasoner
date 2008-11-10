package edu.iastate.pdlreasoner.struct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class RingTest {

	@Test
	public void empty() {
		Ring<String> ring = new Ring<String>(Collections.<String>emptySet());
		assertNull(ring.getNext("dummy"));
	}
	
	@Test
	public void one() {
		String s1 = "one";
		Ring<String> ring = new Ring<String>(Collections.singleton(s1));
		assertEquals(s1, ring.getNext(s1));
		assertNull(ring.getNext("dummy"));
	}

	@Test
	public void three() {
		String[] s = new String[] {"1", "2", "3"};
		Ring<String> ring = new Ring<String>(Arrays.asList(s));
		assertEquals(s[1], ring.getNext(s[0]));
		assertEquals(s[2], ring.getNext(s[1]));
		assertEquals(s[0], ring.getNext(s[2]));
	}

}
