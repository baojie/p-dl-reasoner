package edu.iastate.pdlreasoner;

import java.util.List;

import org.jgroups.ChannelException;

import edu.iastate.pdlreasoner.kb.Query;
import edu.iastate.pdlreasoner.kb.QueryResult;
import edu.iastate.pdlreasoner.master.TableauMaster;
import edu.iastate.pdlreasoner.net.simulated.SimulatedChannelFactory;
import edu.iastate.pdlreasoner.tableau.Tableau;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class PDLReasonerCentralizedWrapper {

	private static final long SLEEP_TIME = 100;
	
	private SimulatedChannelFactory m_ChannelFactory;
	private List<Thread> m_Slaves;

	public PDLReasonerCentralizedWrapper() {
		m_ChannelFactory = new SimulatedChannelFactory();
		m_Slaves = CollectionUtil.makeList();
	}
	
	public QueryResult run(Query query) {
		
		int numSlaves = query.getOntology().getPackages().size();
		for (int i = 0; i < numSlaves; i++) {
			Tableau slave = new Tableau(m_ChannelFactory);
			TableauRunner slaveRunner = new TableauRunner(slave, query);
			m_Slaves.add(new Thread(slaveRunner));
		}
		
		for (Thread slave : m_Slaves) {
			slave.start();
		}
		
		while (m_ChannelFactory.getAllChannelAddresses().size() != numSlaves) {
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
			}
		}
		
		TableauMaster master = new TableauMaster(m_ChannelFactory);
		QueryResult result = null;
		try {
			result = master.run(query);
		} catch (ChannelException e) {
			e.printStackTrace();
		}
		
		for (Thread slave : m_Slaves) {
			while (true) {
				try {
					slave.join();
					break;
				} catch (InterruptedException e) {
				}
			}
		}
		
		return result;
	}

	private static class TableauRunner implements Runnable {

		private Tableau m_Tableau;
		private Query m_Query;

		public TableauRunner(Tableau tab, Query query) {
			m_Tableau = tab;
			m_Query = query;
		}
		
		@Override
		public void run() {
			try {
				m_Tableau.run(m_Query);
			} catch (ChannelException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
