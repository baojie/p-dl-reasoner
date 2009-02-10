package edu.iastate.pdlreasoner;

import java.net.URI;

import org.jgroups.ChannelException;
import org.jgroups.ChannelFactory;
import org.jgroups.JChannel;
import org.jgroups.JChannelFactory;
import org.semanticweb.owl.model.OWLOntologyCreationException;

import edu.iastate.pdlreasoner.exception.IllegalQueryException;
import edu.iastate.pdlreasoner.exception.NotEnoughSlavesException;
import edu.iastate.pdlreasoner.exception.OWLDescriptionNotSupportedException;
import edu.iastate.pdlreasoner.kb.Query;
import edu.iastate.pdlreasoner.kb.QueryResult;
import edu.iastate.pdlreasoner.kb.owlapi.QueryLoader;
import edu.iastate.pdlreasoner.master.TableauMaster;
import edu.iastate.pdlreasoner.tableau.Tableau;
import edu.iastate.pdlreasoner.util.URIUtil;


public class PDLReasoner {

	public static void main(String[] args) throws ChannelException {
		boolean isMaster = false;
		boolean isCentralized = false;
		if (args.length != 2) {
			printUsage();
			System.exit(1);
		}
		
		if ("-m".equalsIgnoreCase(args[0])) {
			isMaster = true;
		} else if ("-s".equalsIgnoreCase(args[0])) {
			isMaster = false;
		} else if ("-c".equalsIgnoreCase(args[0])) {
			isCentralized = true;
		} else {
			printUsage();
			System.exit(1);
		}
		
		String queryPath = args[1];
		URI queryURI = URIUtil.toURI(queryPath);
		
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
		
		if (isCentralized) {
			PDLReasonerCentralizedWrapper reasoner = new PDLReasonerCentralizedWrapper();
			QueryResult result = reasoner.run(query);
			System.out.println(result);
			
		} else {
			ChannelFactory channelFactory = new JChannelFactory(JChannel.DEFAULT_PROTOCOL_STACK);
			
			if (isMaster) {
				TableauMaster master = new TableauMaster(channelFactory);
				QueryResult result = null;
				
				try {
					result = master.run(query);
				} catch (ChannelException e) {
					e.printStackTrace();
				} catch (NotEnoughSlavesException e) {
					e.printStackTrace();
				}
				
				System.out.println(result);
			} else {
				Tableau slave = new Tableau(channelFactory);
	
				try {
					slave.run(query);
				} catch (ChannelException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void printUsage() {
		System.err.println("Usage: java PDLReasoner [-m|-s|-c] query.owl");
		System.err.println("       -m  Execute query as a master reasoner");
		System.err.println("       -s  Execute query as a slave reasoner");
		System.err.println("       -c  Execute query as a centralized reasoner");
	}

}
