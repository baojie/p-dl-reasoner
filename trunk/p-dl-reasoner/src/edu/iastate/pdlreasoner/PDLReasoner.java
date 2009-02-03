package edu.iastate.pdlreasoner;

import java.net.URI;

import org.jgroups.ChannelException;
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

	public static void main(String[] args) {
		boolean isMaster = false;
		if (args.length != 2) {
			printUsage();
			System.exit(1);
		}
		
		if ("-m".equalsIgnoreCase(args[0])) {
			isMaster = true;
		} else if ("-s".equalsIgnoreCase(args[0])) {
			isMaster = false;
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
		
		if (isMaster) {
			TableauMaster master = new TableauMaster();
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
			Tableau slave = new Tableau();

			try {
				slave.run(query);
			} catch (ChannelException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void printUsage() {
		System.err.println("Usage: java PDLReasoner [-m|-s] query.owl");
	}

}
