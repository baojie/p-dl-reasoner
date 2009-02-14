package edu.iastate.pdlreasoner.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Timers {

	private static Map<String,Timer> m_Timers = CollectionUtil.makeMap();
	
	public static void start(String name) {
		Timer timer = m_Timers.get(name);
		if (timer == null) {
			timer = new Timer();
			m_Timers.put(name, timer);
		}
		
		timer.start();
	}
	
	public static void stop(String name) {
		m_Timers.get(name).stop();
	}
	
	public static String printAll() {
		StringBuilder builder = new StringBuilder();
		List<String> keys = CollectionUtil.makeList(m_Timers.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			Timer timer = m_Timers.get(key);
			builder.append(key)
				.append("\t")
				.append(timer.getTotalTime())
				.append("\t");
		}
		
		return builder.toString();
	}
	
	private static class Timer {
		
		private boolean m_IsTiming;
		private long m_StartTime;
		private long m_TotalTime;
		
		public void start() {
			m_IsTiming = true;
			m_StartTime = System.currentTimeMillis();
		}
		
		public void stop() {
			long stopTime = System.currentTimeMillis();
			if (!m_IsTiming) throw new IllegalStateException("Stopping a timer that had not been started.");
			
			m_IsTiming = false;
			m_TotalTime += stopTime - m_StartTime;
		}
		
		public long getTotalTime() {
			return m_TotalTime;
		}
		
	}
	
}
