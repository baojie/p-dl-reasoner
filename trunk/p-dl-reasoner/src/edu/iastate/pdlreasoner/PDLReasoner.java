package edu.iastate.pdlreasoner;

import java.net.URI;

import org.jgroups.ChannelException;
import org.jgroups.ChannelFactory;
import org.jgroups.JChannel;
import org.jgroups.JChannelFactory;
import org.semanticweb.owl.model.OWLOntologyCreationException;

import edu.iastate.pdlreasoner.exception.IllegalQueryException;
import edu.iastate.pdlreasoner.exception.OWLDescriptionNotSupportedException;
import edu.iastate.pdlreasoner.kb.Query;
import edu.iastate.pdlreasoner.kb.QueryResult;
import edu.iastate.pdlreasoner.kb.owlapi.QueryLoader;
import edu.iastate.pdlreasoner.master.TableauMaster;
import edu.iastate.pdlreasoner.net.ChannelUtil;
import edu.iastate.pdlreasoner.tableau.Tableau;
import edu.iastate.pdlreasoner.util.Profiler;
import edu.iastate.pdlreasoner.util.Timers;
import edu.iastate.pdlreasoner.util.URIUtil;


public class PDLReasoner {
	
	private boolean m_IsMaster;
	private boolean m_IsCentralized;
	private String m_QueryPath;
	private boolean m_DoProfiling;

	public static void main(String[] args) throws ChannelException {
		PDLReasoner reasoner = new PDLReasoner();
		for (int i = 0; i < args.length; i++) {
			String arg = args[i].trim();

			if (arg.equalsIgnoreCase("-m")) {
				reasoner.m_IsMaster = true;
			} else if (arg.equalsIgnoreCase("-s")) {
				reasoner.m_IsMaster = false;
			} else if (arg.equalsIgnoreCase("-c")) {
				reasoner.m_IsCentralized = true;
			} else if (arg.equalsIgnoreCase("-t")) {
				reasoner.m_DoProfiling = true;
			} else {
				if (i != args.length - 1) {
					printUsage();
					System.exit(1);
				}
				
				reasoner.m_QueryPath = arg;
			}
		}
		
		reasoner.run();
	}
	
	private static void printUsage() {
		System.err.println("Usage: java PDLReasoner -m|-s|-c [-t] query.owl");
		System.err.println("       -m  Execute query as a master reasoner");
		System.err.println("       -s  Execute query as a slave reasoner");
		System.err.println("       -c  Execute query as a centralized reasoner");
		System.err.println("       -t  Record and print timings");
	}

	private void run() throws ChannelException {
		Timers.start("load");
		URI queryURI = URIUtil.toURI(m_QueryPath);
		
		Query query = null;
		try {
			query = new QueryLoader().loadQuery(queryURI);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IllegalQueryException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (OWLDescriptionNotSupportedException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		if (!query.isUnderstandableByWitness()) {
			System.err.println("Error: Query is not understandable by the specified witness package.");
			System.exit(1);
		}
		
		Timers.stop("load");
		
		ChannelUtil.setSessionName(m_QueryPath);
		
		QueryResult result = null;
		if (m_IsCentralized) {
			PDLReasonerCentralizedWrapper reasoner = new PDLReasonerCentralizedWrapper();
			result = reasoner.run(query);
			
		} else {
			ChannelFactory channelFactory = new JChannelFactory(JChannel.DEFAULT_PROTOCOL_STACK);
			
			if (m_IsMaster) {
				TableauMaster master = new TableauMaster(channelFactory);
				
				try {
					result = master.run(query);
				} catch (ChannelException e) {
					e.printStackTrace();
				}
				
			} else {
				Tableau slave = new Tableau(channelFactory);
	
				try {
					slave.run(query);
				} catch (ChannelException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (m_IsCentralized || m_IsMaster) {
			if (m_DoProfiling) {
				System.out.print(m_QueryPath);
				System.out.print(",");
				System.out.print(Timers.printAll());
				System.out.print(Profiler.INSTANCE.printAll());
				System.out.println(result.toShortString());;
			} else {
				System.out.println(result);
			}
		}
	}

}
