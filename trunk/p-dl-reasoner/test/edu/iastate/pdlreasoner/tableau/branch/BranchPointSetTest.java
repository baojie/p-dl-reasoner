package edu.iastate.pdlreasoner.tableau.branch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class BranchPointSetTest {
	
	private BranchPoint[] m_BPs;
	private BranchPointSet[] m_BPSets;
	private BranchPointSet m_BPSet;
	
	@Before
	public void setUp() {
		m_BPs = new BranchPoint[5];
		m_BPSets = new BranchPointSet[m_BPs.length];
		for (int i = 0; i < m_BPs.length; i++) {
			m_BPs[i] = new BranchPoint(i);
			m_BPSets[i] = new BranchPointSet(m_BPs[i]);
		}
		m_BPSet = new BranchPointSet();
	}

	@Test
	public void empty() {
		assertTrue(m_BPSet.isEmpty());
		assertNull(m_BPSet.getLatestBranchPoint());
		assertFalse(m_BPSet.hasSameOrAfter(m_BPs[0]));
		m_BPSet.remove(m_BPs[0]);
	}
	
	@Test
	public void union() {
		m_BPSet.union(m_BPSets[0]);
		assertEquals(m_BPSets[0], m_BPSet);
		assertFalse(m_BPSet.isEmpty());
		assertEquals(m_BPs[0], m_BPSet.getLatestBranchPoint());
		assertTrue(m_BPSet.hasSameOrAfter(m_BPs[0]));
		assertFalse(m_BPSet.hasSameOrAfter(m_BPs[1]));
		
		m_BPSet.remove(m_BPs[0]);
		assertTrue(m_BPSet.isEmpty());
	}

	@Test
	public void union2() {
		final int BP0 = 0;
		final int BP3 = 3;
		m_BPSet = m_BPSets[BP0];
		m_BPSet.union(m_BPSets[BP3]);
		assertEquals(m_BPs[BP3], m_BPSet.getLatestBranchPoint());
		for (int i = 0; i <= BP3; i++) {
			assertTrue(m_BPSet.hasSameOrAfter(m_BPs[i]));
		}
		assertFalse(m_BPSet.hasSameOrAfter(m_BPs[BP3 + 1]));
		
		m_BPSet.remove(m_BPs[BP0]);
		assertEquals(m_BPSets[BP3], m_BPSet);
	}

	@Test
	public void staticUnion() {
		m_BPSet = BranchPointSet.union(m_BPSets[1], m_BPSets[3]);
		
		BranchPointSet bp = new BranchPointSet(m_BPs[1]);
		bp.union(new BranchPointSet(m_BPs[3]));
		assertEquals(bp, m_BPSet);
	}
	
	@Test
	public void comparable() {
		BranchPointSet bps123 = BranchPointSet.union(m_BPSets[1], m_BPSets[3], m_BPSets[2]);
		BranchPointSet bps04 = BranchPointSet.union(m_BPSets[0], m_BPSets[4]);
		assertEquals(bps123, Collections.min(Arrays.asList(bps123, bps04), BranchPointSet.ORDER_BY_LATEST_BRANCH_POINT));
	}
}
