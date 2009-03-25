package edu.iastate.pdlreasoner;

import java.util.List;

import org.jgroups.ChannelException;

import edu.iastate.pdlreasoner.exception.IllegalQueryException;
import edu.iastate.pdlreasoner.kb.OntologyPackage;
import edu.iastate.pdlreasoner.kb.Query;
import edu.iastate.pdlreasoner.kb.QueryResult;
import edu.iastate.pdlreasoner.master.TableauMaster;
import edu.iastate.pdlreasoner.net.simulated.SimulatedChannelFactory;
import edu.iastate.pdlreasoner.tableau.Tableau;
import edu.iastate.pdlreasoner.util.CollectionUtil;
import edu.iastate.pdlreasoner.util.Timers;

public class PDLReasonerCentralizedWrapper {

	private static final long SLEEP_TIME = 100;
	
	private Timers m_MasterTimers;
	private SimulatedChannelFactory m_ChannelFactory;
	private List<Thread> m_Slaves;
	
	public PDLReasonerCentralizedWrapper() {
		m_MasterTimers = new Timers();
		m_ChannelFactory = new SimulatedChannelFactory();
		m_Slaves = CollectionUtil.makeList();
	}

	public void setTimers(Timers timers) {
		m_MasterTimers = timers;
	}

	public QueryResult run(Query query, List<OntologyPackage> ontologies) {
		int numSlaves = ontologies.size();
		for (OntologyPackage ontology : ontologies) {
			Tableau slave = new Tableau(m_ChannelFactory);
			TableauRunner slaveRunner = new TableauRunner(slave, ontology);
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
		master.setTimers(m_MasterTimers);
		QueryResult result = null;
		try {
			result = master.run(query, numSlaves);
		} catch (ChannelException e) {
			e.printStackTrace();
		} catch (IllegalQueryException e) {
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
		private OntologyPackage m_Ontology;

		public TableauRunner(Tableau tab, OntologyPackage ontology) {
			m_Tableau = tab;
			m_Ontology = ontology;
		}
		
		@Override
		public void run() {
			try {
				m_Tableau.run(m_Ontology);
			} catch (ChannelException e) {
				e.printStackTrace();
			}
		}
		
	}

}
