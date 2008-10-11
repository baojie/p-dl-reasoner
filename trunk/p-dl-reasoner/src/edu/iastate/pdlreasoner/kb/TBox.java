package edu.iastate.pdlreasoner.kb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.iastate.pdlreasoner.model.AllValues;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.SomeValues;
import edu.iastate.pdlreasoner.model.Subclass;
import edu.iastate.pdlreasoner.model.Top;
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
		ExternalConceptsExtractor visitor = new ExternalConceptsExtractor();
		for (Subclass axiom : m_Axioms) {
			axiom.getSub().accept(visitor);
			axiom.getSup().accept(visitor);
		}
				
		return visitor.getExternalConcepts();
	}
	
	private static class ExternalConceptsExtractor implements ConceptVisitor {
		
		private MultiValuedMap<DLPackage, Concept> m_ExternalConcepts;
		
		public ExternalConceptsExtractor() {
			m_ExternalConcepts = new MultiValuedMap<DLPackage, Concept>();
		}
		
		public MultiValuedMap<DLPackage, Concept> getExternalConcepts() {
			return m_ExternalConcepts;
		}
		
		@Override
		public void visit(Bottom bottom) {
		}

		@Override
		public void visit(Top top) {
		}

		@Override
		public void visit(Atom atom) {
		}

		@Override
		public void visit(Negation negation) {
		}

		@Override
		public void visit(And and) {
		}

		@Override
		public void visit(Or or) {
		}

		@Override
		public void visit(SomeValues someValues) {
		}

		@Override
		public void visit(AllValues allValues) {
		}
		
	}
	
}
