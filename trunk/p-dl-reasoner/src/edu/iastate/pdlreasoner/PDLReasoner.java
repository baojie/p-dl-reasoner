package edu.iastate.pdlreasoner;

import java.net.URI;

import org.jgroups.ChannelException;
import org.jgroups.ChannelFactory;
import org.jgroups.JChannel;
import org.jgroups.JChannelFactory;
import org.semanticweb.owl.model.OWLOntologyCreationException;

import edu.iastate.pdlreasoner.exception.IllegalQueryException;
import edu.iastate.pdlreasoner.exception.OWLDescriptionNotSupportedException;
import edu.iastate.pdlreasoner.kb.OntologyPackage;
import edu.iastate.pdlreasoner.kb.Query;
import edu.iastate.pdlreasoner.kb.QueryResult;
import edu.iastate.pdlreasoner.kb.owlapi.OntologyLoader;
import edu.iastate.pdlreasoner.kb.owlapi.QueryLoader;
import edu.iastate.pdlreasoner.master.TableauMaster;
import edu.iastate.pdlreasoner.net.ChannelUtil;
import edu.iastate.pdlreasoner.tableau.Tableau;
import edu.iastate.pdlreasoner.util.Profiler;
import edu.iastate.pdlreasoner.util.Timers;
import edu.iastate.pdlreasoner.util.URIUtil;


public class PDLReasoner {
	
	private boolean m_IsCentralized;
	private boolean m_IsMaster;
	private int m_NumSlaves;
	private String m_OntologyPath;
	private String m_QueryPath;
	private String m_Witness;
	private boolean m_DoProfiling;

	public static void main(String[] args) throws ChannelException {
		PDLReasoner reasoner = new PDLReasoner();
		for (int i = 0; i < args.length; i++) {
			String arg = args[i].trim();

			if (arg.equalsIgnoreCase("-m")) {
				reasoner.m_IsMaster = true;
				if (++i >= args.length) {
					printUsage();
					System.exit(1);
				}
				
				reasoner.m_NumSlaves = Integer.parseInt(args[i]);
			} else if (arg.equalsIgnoreCase("-s")) {
				reasoner.m_IsMaster = false;
				if (++i >= args.length) {
					printUsage();
					System.exit(1);
				}
				
				reasoner.m_OntologyPath = args[i].trim();
//			} else if (arg.equalsIgnoreCase("-c")) {
//				reasoner.m_IsCentralized = true;
			} else if (arg.equalsIgnoreCase("-t")) {
				reasoner.m_DoProfiling = true;
			} else {
				if (i != args.length - 2) {
					printUsage();
					System.exit(1);
				}
				
				reasoner.m_QueryPath = arg;
				reasoner.m_Witness = args[i + 1].trim();
			}
		}
		
		reasoner.run();
	}
	
	private static void printUsage() {
		System.err.println("Usage: java PDLReasoner [OPTIONS] query.owl witnessURI");
		System.err.println("  OPTIONS:");
		System.err.println("       -m N             Execute as master and waits for N slaves");
		System.err.println("       -s ontology.owl  Execute as slave with an ontology");
		//System.err.println("       -c  Execute query as a centralized reasoner");
		System.err.println("       -t               Record and print timings");
	}

	private void run() throws ChannelException {
		ChannelUtil.setSessionName(m_QueryPath);
		
		QueryResult result = null;
		if (m_IsCentralized) {
//			PDLReasonerCentralizedWrapper reasoner = new PDLReasonerCentralizedWrapper();
//			result = reasoner.run(query);
			
		} else {
			ChannelFactory channelFactory = new JChannelFactory(JChannel.DEFAULT_PROTOCOL_STACK);
			
			if (m_IsMaster) {
				URI queryURI = URIUtil.toURI(m_QueryPath);
				URI witnessURI = URIUtil.toURI(m_Witness);
				
				Query query = null;
				try {
					query = new QueryLoader().loadQuery(queryURI, witnessURI);
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

				TableauMaster master = new TableauMaster(channelFactory);
				
				try {
					result = master.run(query, m_NumSlaves);
				} catch (ChannelException e) {
					e.printStackTrace();
				} catch (IllegalQueryException e) {
					System.err.println(e);
					System.exit(1);
				}
				
			} else {
				URI ontologyURI = URIUtil.toURI(m_OntologyPath);
				
				OntologyPackage ontology = null;
				try {
					ontology = new OntologyLoader().loadOntology(ontologyURI);
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
					System.exit(1);
				} catch (OWLDescriptionNotSupportedException e) {
					e.printStackTrace();
					System.exit(1);
				}
				
				Tableau slave = new Tableau(channelFactory);
	
				try {
					slave.run(ontology);
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
