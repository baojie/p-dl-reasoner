package edu.iastate.pdlreasoner.kb;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.ModelFactory;
import edu.iastate.pdlreasoner.model.Subclass;
import edu.iastate.pdlreasoner.model.visitor.ExternalConceptsExtractor;
import edu.iastate.pdlreasoner.model.visitor.NNFConverter;
import edu.iastate.pdlreasoner.struct.MultiValuedMap;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class TBox {

	private static final Logger LOGGER = Logger.getLogger(TBox.class);
	
	//Constants
	private KnowledgeBase m_HomeKB;
	
	//Variables
	private List<Subclass> m_Axioms;
	
	//Caches
	private List<Concept> m_UC;
	
	public TBox(KnowledgeBase homeKB) {
		m_HomeKB = homeKB;
		m_Axioms = CollectionUtil.makeList();
	}

	public void addAxiom(Subclass axiom) {
		m_Axioms.add(axiom);
	}
	
	public void init() {
		normalizeAxioms();
		
		m_UC = CollectionUtil.makeList();
		DLPackage homePackage = m_HomeKB.getPackage();
		NNFConverter converter = new NNFConverter(homePackage);
		for (Subclass subclass : m_Axioms) {
			Concept notSub = ModelFactory.makeNegation(homePackage, subclass.getSub());
			Concept nnfNotSub = converter.convert(notSub);
			Concept uc = ModelFactory.makeOr(nnfNotSub, subclass.getSup());
			m_UC.add(uc);
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(m_HomeKB.getPackage().toDebugString() + "UC = " + m_UC);
		}
	}
	
	public List<Concept> getUC() {
		return m_UC;
	}

	private void normalizeAxioms() {
		NNFConverter converter = new NNFConverter(m_HomeKB.getPackage());
		List<Subclass> nnfAxioms = new ArrayList<Subclass>();
		for (Subclass subclass : m_Axioms) {
			Concept nnfSub = converter.convert(subclass.getSub());
			Concept nnfSup = converter.convert(subclass.getSup());
			nnfAxioms.add(ModelFactory.makeSub(nnfSub, nnfSup));
		}
		m_Axioms = nnfAxioms;
	}
	
	// Assumption: normalizeAxioms() has been called
	public MultiValuedMap<DLPackage, Concept> getExternalConcepts() {
		DLPackage homePackage = m_HomeKB.getPackage();
		ExternalConceptsExtractor visitor = new ExternalConceptsExtractor(homePackage);
		for (Subclass axiom : m_Axioms) {
			axiom.getSub().accept(visitor);
			axiom.getSup().accept(visitor);
		}
				
		return visitor.getExternalConcepts();
	}
	
}
