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
import java.util.List;

import org.jgroups.Address;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import edu.iastate.pdlreasoner.exception.NotEnoughSlavesException;
import edu.iastate.pdlreasoner.kb.KnowledgeBase;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.Role;
import edu.iastate.pdlreasoner.model.Top;
import edu.iastate.pdlreasoner.util.CollectionUtil;


public class PDLReasoner {

	private boolean m_IsMaster;
	private List<KnowledgeBase> m_KBs;
	private Concept m_Query;
	private DLPackage m_QueryWitness;
	
	private Address m_MasterAdd;
	private DLPackage m_AssignedPackage;
	
	public void setIsMaster(boolean isMaster) {
		m_IsMaster = isMaster;
	}
	
	public void setKBs(List<KnowledgeBase> kbs) {
		m_KBs = kbs;
	}

	public void setQuery(Concept query) {
		m_Query = query;
	}

	public void setQueryWitness(DLPackage queryWitness) {
		m_QueryWitness = queryWitness;
	}
	
	public void run() throws ChannelException, NotEnoughSlavesException {
		if (m_IsMaster) {
			runMaster();
		} else {
			runSlave();
		}
	}
	
	private void runMaster() throws ChannelException, NotEnoughSlavesException {
		JChannel channel = new JChannel();
		channel.connect(getSessionName());
		View view = channel.getView();
		Address masterAdd = channel.getLocalAddress();
		List<Address> members = CollectionUtil.makeList(view.getMembers());
		members.remove(masterAdd);
		if (members.size() < m_KBs.size()) {
			channel.close();
			throw new NotEnoughSlavesException("Ontology has " + m_KBs.size() + " packages but only " + members.size() + " slaves are available.");
		}

		for (int i = 0; i < m_KBs.size(); i++) {
			Message msg = new Message(members.get(i), masterAdd, m_KBs.get(i).getPackage());
			channel.send(msg);
		}
		
		//channel.close();
	}
	
	private void runSlave() throws ChannelException {
		JChannel channel = new JChannel();
		channel.connect(getSessionName());
	
		channel.setReceiver(new ReceiverAdapter() {
				@Override
				public void receive(Message msg) {
					m_MasterAdd = msg.getSrc();
					m_AssignedPackage = (DLPackage) msg.getObject();
					
					System.out.println(m_MasterAdd);
					System.out.println(m_AssignedPackage);
				}
			});
	}

	private String getSessionName() {
		return "PDL";
	}
	
	
	public void setExample1() {
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

		setKBs(Arrays.asList(kbs));
		setQuery(p0Top);
		setQueryWitness(p[0]);
	}
	
	

	public static void main(String[] args) {
		PDLReasoner reasoner = new PDLReasoner();
		
		if ("-m".equalsIgnoreCase(args[0])) {
			reasoner.setIsMaster(true);
		} else if ("-s".equalsIgnoreCase(args[0])) {
			reasoner.setIsMaster(false);
		} else {
			printUsage();
			System.exit(1);
		}
		
		reasoner.setExample1();
		
		try {
			reasoner.run();
		} catch (ChannelException e) {
			e.printStackTrace();
		} catch (NotEnoughSlavesException e) {
			e.printStackTrace();
		}
	}
	
	private static void printUsage() {
		System.out.println("Usage: java PDLReasoner [-m|-s]");
	}

}
