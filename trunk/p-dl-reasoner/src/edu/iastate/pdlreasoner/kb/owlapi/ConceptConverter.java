package edu.iastate.pdlreasoner.kb.owlapi;

import java.net.URI;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDescriptionVisitor;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectComplementOf;
import org.semanticweb.owl.model.OWLObjectExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectIntersectionOf;
import org.semanticweb.owl.model.OWLObjectMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectOneOf;
import org.semanticweb.owl.model.OWLObjectSelfRestriction;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectUnionOf;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

import edu.iastate.pdlreasoner.exception.OWLDescriptionNotSupportedException;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.PackageID;

public class ConceptConverter {

	private OntologyPackageIDStore m_PackageIDStore;
	private ConceptConverterImp m_Parser;
	private PackageID m_CurrentPackageID;
	
	public ConceptConverter(OntologyPackageIDStore packageIDStore) {
		m_PackageIDStore = packageIDStore;
		m_Parser = new ConceptConverterImp();
	}
	
	public void setPackageID(PackageID packageID) {
		m_CurrentPackageID = packageID;
	}
	
	public Concept parse(OWLDescription desc) throws OWLDescriptionNotSupportedException {
		m_Parser.reset();
		desc.accept(m_Parser);
		Concept concept = m_Parser.getConcept();
		if (concept == null) throw new OWLDescriptionNotSupportedException(desc.toString() + " not supported.");

		return concept;
	}
	
	
	private static class ConceptConverterImp implements OWLDescriptionVisitor {

		private Concept m_Result;
		
		public void reset() {
			m_Result = null;
		}
		
		public Concept getConcept() {
			return m_Result;
		}
		
		@Override
		public void visit(OWLClass desc) {
			URI uri = desc.getURI();
			if (uri.equals(OWLRDFVocabulary.OWL_THING.getURI())) {
				
			} else if (uri.equals(OWLRDFVocabulary.OWL_NOTHING.getURI())) {
				
			} else {
				
			}
		}

		@Override
		public void visit(OWLObjectIntersectionOf desc) {
		}

		@Override
		public void visit(OWLObjectUnionOf desc) {
		}

		@Override
		public void visit(OWLObjectComplementOf desc) {
		}

		@Override
		public void visit(OWLObjectSomeRestriction desc) {
		}

		@Override
		public void visit(OWLObjectAllRestriction desc) {
		}

		@Override
		public void visit(OWLObjectValueRestriction desc) {
			visitUnsupported();
		}

		@Override
		public void visit(OWLObjectMinCardinalityRestriction desc) {
			visitUnsupported();
		}

		@Override
		public void visit(OWLObjectExactCardinalityRestriction desc) {
			visitUnsupported();
		}

		@Override
		public void visit(OWLObjectMaxCardinalityRestriction desc) {
			visitUnsupported();
		}

		@Override
		public void visit(OWLObjectSelfRestriction desc) {
			visitUnsupported();
		}

		@Override
		public void visit(OWLObjectOneOf desc) {
			visitUnsupported();
		}

		@Override
		public void visit(OWLDataSomeRestriction desc) {
			visitUnsupported();
		}

		@Override
		public void visit(OWLDataAllRestriction desc) {
			visitUnsupported();
		}

		@Override
		public void visit(OWLDataValueRestriction desc) {
			visitUnsupported();
		}

		@Override
		public void visit(OWLDataMinCardinalityRestriction desc) {
			visitUnsupported();
		}

		@Override
		public void visit(OWLDataExactCardinalityRestriction desc) {
			visitUnsupported();
		}

		@Override
		public void visit(OWLDataMaxCardinalityRestriction desc) {
			visitUnsupported();
		}

		private void visitUnsupported() {
			m_Result = null;
		}

	}

}