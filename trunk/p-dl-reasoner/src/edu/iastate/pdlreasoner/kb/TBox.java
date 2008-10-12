package edu.iastate.pdlreasoner.kb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.iastate.pdlreasoner.model.AllValues;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.ContextualizedConcept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.SomeValues;
import edu.iastate.pdlreasoner.model.Subclass;
import edu.iastate.pdlreasoner.model.Top;
import edu.iastate.pdlreasoner.model.visitor.ConceptTraverser;
import edu.iastate.pdlreasoner.model.visitor.ConceptVisitor;
import edu.iastate.pdlreasoner.struct.MultiValuedMap;

public class TBox {

	private KnowledgeBase m_HomeKB;
	private List<Subclass> m_Axioms;
	
	public TBox(KnowledgeBase homeKB) {
		m_HomeKB = homeKB;
		m_Axioms = new ArrayList<Subclass>();
	}

	public void addAxiom(Subclass axiom) {
		m_Axioms.add(axiom);
	}
	
	public void normalizeAxioms() {
		
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
	
	private static class ExternalConceptsExtractor extends ConceptTraverser {
		
		private final DLPackage m_HomePackage;
		private MultiValuedMap<DLPackage, Concept> m_ExternalConcepts;
		
		public ExternalConceptsExtractor(DLPackage homePackage) {
			m_HomePackage = homePackage;
			m_ExternalConcepts = new MultiValuedMap<DLPackage, Concept>();
		}
		
		public MultiValuedMap<DLPackage, Concept> getExternalConcepts() {
			return m_ExternalConcepts;
		}
		
		private void visitContextualizedAtom(ContextualizedConcept c) {
			DLPackage context = c.getContext();
			if (!m_HomePackage.equals(context)) {
				m_ExternalConcepts.add(context, c);				
			}
		}
		
		@Override
		public void visit(Top top) {
			visitContextualizedAtom(top);
		}

		@Override
		public void visit(Atom atom) {
			visitContextualizedAtom(atom);
		}

	}
	
}
