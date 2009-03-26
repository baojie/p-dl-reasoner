package edu.iastate.pdlreasoner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.List;

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
import edu.iastate.pdlreasoner.util.CollectionUtil;
import edu.iastate.pdlreasoner.util.Profiler;
import edu.iastate.pdlreasoner.util.Timers;
import edu.iastate.pdlreasoner.util.URIUtil;


public class PDLReasoner {
	
	private boolean m_IsMaster;
	private int m_NumSlaves;
	private String m_OntologyPath;
	
	private boolean m_IsCentralized;
	private String m_OntologyListPath;
	
	private String m_QueryPath;
	private String m_WitnessPath;
	private boolean m_DoProfiling;

	public static void main(String[] args) throws ChannelException, IOException {
		if (args.length == 0) {
			printUsage();
			System.exit(1);
		}
		
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
			} else if (arg.equalsIgnoreCase("-c")) {
				reasoner.m_IsCentralized = true;
				if (++i >= args.length) {
					printUsage();
					System.exit(1);
				}
				
				reasoner.m_OntologyListPath = args[i].trim();
			} else if (arg.equalsIgnoreCase("-t")) {
				reasoner.m_DoProfiling = true;
			} else {
				if (i != args.length - 2) {
					printUsage();
					System.exit(1);
				}
				
				reasoner.m_QueryPath = arg;
				i++;
				reasoner.m_WitnessPath = args[i].trim();
			}
		}
		
		reasoner.run();
	}
	
	private static void printUsage() {
		System.err.println("Usage: java PDLReasoner [OPTIONS] query.owl witnessURI");
		System.err.println("  OPTIONS:");
		System.err.println("       -m N              Execute as master and waits for N slaves");
		System.err.println("       -s ontology.owl   Execute as slave with an ontology");
		System.err.println("       -c ontology_list  Execute query as a centralized, multi-threaded reasoner");
		System.err.println("       -t                Record and print timings");
	}

	private static Query loadQuery(String queryPath, String witnessPath) {
		URI queryURI = URIUtil.toURI(queryPath);
		URI witnessURI = URIUtil.toURI(witnessPath);
		
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
		return query;
	}

	private static OntologyPackage loadOntology(String ontologyPath) {
		URI ontologyURI = URIUtil.toURI(ontologyPath);
		
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
		
		return ontology;
	}
	
	private static List<OntologyPackage> loadOntologies(String ontologyListPath) throws IOException {
		List<OntologyPackage> ontologies = CollectionUtil.makeList();
		
		BufferedReader in = new BufferedReader(new FileReader(new File(ontologyListPath)));
		String line;
		while ((line = in.readLine()) != null) {
			ontologies.add(loadOntology(line));
		}
		in.close();
		
		return ontologies;
	}

	private void run() throws ChannelException, IOException {
		ChannelUtil.setSessionName(m_QueryPath);
		
		Timers timers = new Timers();
		QueryResult result = null;
		if (m_IsCentralized) {
			timers.start("load");
			Query query = loadQuery(m_QueryPath, m_WitnessPath);
			List<OntologyPackage> ontologies = loadOntologies(m_OntologyListPath);
			timers.stop("load");
			
			PDLReasonerCentralizedWrapper reasoner = new PDLReasonerCentralizedWrapper();
			reasoner.setTimers(timers);
			result = reasoner.run(query, ontologies);
			
		} else {
			ChannelFactory channelFactory = new JChannelFactory(JChannel.DEFAULT_PROTOCOL_STACK);
			
			if (m_IsMaster) {
				timers.start("load");
				Query query = loadQuery(m_QueryPath, m_WitnessPath);
				timers.stop("load");
				
				TableauMaster master = new TableauMaster(channelFactory);
				master.setTimers(timers);
				
				try {
					result = master.run(query, m_NumSlaves);
				} catch (ChannelException e) {
					e.printStackTrace();
				} catch (IllegalQueryException e) {
					System.err.println(e);
					System.exit(1);
				}
				
			} else {
				timers.start("load");
				OntologyPackage ontology = loadOntology(m_OntologyPath);
				timers.stop("load");
				
				Tableau slave = new Tableau(channelFactory);
				slave.setTimers(timers);
				
				try {
					slave.run(ontology);
				} catch (ChannelException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (m_DoProfiling) {
			System.out.print(m_QueryPath);
			System.out.print(",");
			System.out.print(timers.printAll());
			System.out.print(Profiler.INSTANCE.printAll());
			System.out.println(result.toShortString());;
		} else if (m_IsCentralized || m_IsMaster) {
			System.out.println(result);
		}
	}
	
}
