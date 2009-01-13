package edu.iastate.pdlreasoner;

import static edu.iastate.pdlreasoner.model.ModelFactory.makeAllValues;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeAnd;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeAtom;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeNegation;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeOr;
import static edu.iastate.pdlreasoner.model.ModelFactory.makePackage;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeRole;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeSomeValues;
import static edu.iastate.pdlreasoner.model.ModelFactory.makeTop;

import java.net.URI;
import java.util.Arrays;

import org.jgroups.ChannelException;

import edu.iastate.pdlreasoner.exception.NotEnoughSlavesException;
import edu.iastate.pdlreasoner.kb.KnowledgeBase;
import edu.iastate.pdlreasoner.kb.Query;
import edu.iastate.pdlreasoner.kb.QueryResult;
import edu.iastate.pdlreasoner.master.TableauMaster;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.Role;
import edu.iastate.pdlreasoner.model.Top;
import edu.iastate.pdlreasoner.tableau.Tableau;


public class PDLReasoner {

	public static void main(String[] args) {
		boolean isMaster = false;
		if ("-m".equalsIgnoreCase(args[0])) {
			isMaster = true;
		} else if ("-s".equalsIgnoreCase(args[0])) {
			isMaster = false;
		} else {
			printUsage();
			System.exit(1);
		}
		
		Query query = getExample1();
		
		if (isMaster) {
			TableauMaster master = new TableauMaster();
			QueryResult result = null;
			
			try {
				result = master.run(query);
			} catch (ChannelException e) {
				e.printStackTrace();
			} catch (NotEnoughSlavesException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println(result);
		} else {
			Tableau slave = new Tableau();

			try {
				slave.run(query);
			} catch (ChannelException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void printUsage() {
		System.out.println("Usage: java PDLReasoner [-m|-s]");
	}

	private static Query getExample1() {
		DLPackage[] p;
		KnowledgeBase[] kbs;
		
		p = new DLPackage[3];
		for (int i = 0; i < p.length; i++) {
			p[i] = makePackage(URI.create("#package" + i));
		}
		
		kbs = new KnowledgeBase[p.length];
		for (int i = 0; i < kbs.length; i++) {
			kbs[i] = new KnowledgeBase(p[i]);
		}
		
		Top p0Top = makeTop(p[0]);
		Role r = makeRole(URI.create("#r"));
		Atom p0C = makeAtom(p[0], URI.create("#C"));
		
		Atom p1D1 = makeAtom(p[1], URI.create("#D1"));
		Atom p1D2 = makeAtom(p[1], URI.create("#D2"));
		Atom p1D3 = makeAtom(p[1], URI.create("#D3"));
		
		kbs[0].addAxiom(p0Top, p1D3);
		
		And bigAnd = makeAnd(
				p1D1,
				makeSomeValues(r, p0C),
				makeAllValues(r, makeNegation(p[0], p0C))
			);
		Or bigOr = makeOr(bigAnd, makeNegation(p[1], p1D2));
		kbs[0].addAxiom(p0Top, bigOr);
		
		kbs[1].addAxiom(p1D1, p1D2);

		return new Query(Arrays.asList(kbs), p0Top, p[0]);
	}

}
