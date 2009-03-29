package edu.iastate.pdlreasoner.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Timers {

	private Map<String,Timer> m_Timers = CollectionUtil.makeMap();
	
	public void start(String name) {
		Timer timer = m_Timers.get(name);
		if (timer == null) {
			timer = new Timer();
			m_Timers.put(name, timer);
		}
		
		timer.start();
	}
	
	public void stop(String name) {
		m_Timers.get(name).stop();
	}
	
	public void reset(String name) {
		Timer timer = m_Timers.get(name);
		if (timer != null) {
			timer.reset();
		}
	}
	
	public String printAll() {
		StringBuilder builder = new StringBuilder();
		List<String> keys = CollectionUtil.makeList(m_Timers.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			Timer timer = m_Timers.get(key);
			builder.append(key)
				.append("=")
				.append(timer.getTotalTime())
				.append(",");
		}
		
		return builder.toString();
	}
	
	private static class Timer {
		
		private boolean m_IsTiming;
		private long m_StartTime;
		private long m_TotalTime;
		
		public void start() {
			m_IsTiming = true;
			m_StartTime = System.nanoTime();
		}
		
		public void stop() {
			long stopTime = System.nanoTime();
			if (!m_IsTiming) throw new IllegalStateException("Stopping a timer that had not been started.");
			
			m_IsTiming = false;
			m_TotalTime += stopTime - m_StartTime;
		}
		
		public void reset() {
			m_TotalTime = 0L;
		}

		public long getTotalTime() {
			return m_TotalTime;
		}
		
	}

}
