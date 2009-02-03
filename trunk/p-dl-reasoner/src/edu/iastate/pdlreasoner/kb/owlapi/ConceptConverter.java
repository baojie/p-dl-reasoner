package edu.iastate.pdlreasoner.kb.owlapi;

import java.net.URI;
import java.util.Set;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDescriptionVisitor;
import org.semanticweb.owl.model.OWLNaryBooleanDescription;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectComplementOf;
import org.semanticweb.owl.model.OWLObjectExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectIntersectionOf;
import org.semanticweb.owl.model.OWLObjectMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectOneOf;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLObjectSelfRestriction;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectUnionOf;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.model.OWLRestriction;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

import edu.iastate.pdlreasoner.exception.OWLDescriptionNotSupportedException;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.ModelFactory;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.model.Role;

public class ConceptConverter {

	private OntologyPackageIDStore m_PackageIDStore;
	private ConceptConverterImp m_Converter;
	private PackageID m_CurrentPackageID;
	
	public ConceptConverter(OntologyPackageIDStore packageIDStore) {
		m_PackageIDStore = packageIDStore;
		m_Converter = new ConceptConverterImp();
	}
	
	public void setPackageID(PackageID packageID) {
		m_CurrentPackageID = packageID;
	}
	
	public Concept convert(OWLDescription desc) throws OWLDescriptionNotSupportedException {
		m_Converter.reset();
		desc.accept(m_Converter);
		Concept concept = m_Converter.getConcept();
		if (concept == null) throw new OWLDescriptionNotSupportedException(desc.toString() + " not supported.");

		return concept;
	}
	
	
	private class ConceptConverterImp implements OWLDescriptionVisitor {

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
				m_Result = ModelFactory.makeTop(m_CurrentPackageID);
			} else if (uri.equals(OWLRDFVocabulary.OWL_NOTHING.getURI())) {
				m_Result = Bottom.INSTANCE;
			} else {
				PackageID packageID = m_PackageIDStore.getPackageID(uri);
				if (packageID == null) {
					m_Result = null; return;
				}
				
				m_Result = ModelFactory.makeAtom(packageID, uri.getFragment());
			}
		}

		@Override
		public void visit(OWLObjectIntersectionOf desc) {
			Concept[] convertedOps = visitNary(desc);
			if (convertedOps == null) {
				m_Result = null; return;
			}
			
			m_Result = ModelFactory.makeAnd(convertedOps);
		}

		@Override
		public void visit(OWLObjectUnionOf desc) {
			Concept[] convertedOps = visitNary(desc);
			if (convertedOps == null) {
				m_Result = null; return;
			}
			
			m_Result = ModelFactory.makeOr(convertedOps);
		}

		@Override
		public void visit(OWLObjectComplementOf desc) {
			desc.getOperand().accept(this);
			if (m_Result == null) return;
			
			m_Result = ModelFactory.makeNegation(m_CurrentPackageID, m_Result);
		}

		@Override
		public void visit(OWLObjectSomeRestriction desc) {
			desc.getFiller().accept(this);
			if (m_Result == null) return;
			
			Role role = visitObjectProperty(desc);
			if (role == null) {
				m_Result = null; return;
			}
			
			m_Result = ModelFactory.makeSomeValues(role, m_Result);
		}

		@Override
		public void visit(OWLObjectAllRestriction desc) {
			desc.getFiller().accept(this);
			if (m_Result == null) return;
			
			Role role = visitObjectProperty(desc);
			if (role == null) {
				m_Result = null; return;
			}
			
			m_Result = ModelFactory.makeAllValues(role, m_Result);
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
		
		private Concept[] visitNary(OWLNaryBooleanDescription desc) {
			Set<OWLDescription> ops = desc.getOperands();
			Concept[] convertedOps = new Concept[ops.size()];
			{
				int i = 0;
				for (OWLDescription op : ops) {
					op.accept(this);
					if (m_Result == null) return null;
					convertedOps[i++] = m_Result;
				}
			}
			
			return convertedOps;
		}
		
		private Role visitObjectProperty(OWLRestriction<OWLObjectPropertyExpression> res) {
			OWLObjectPropertyExpression property = res.getProperty();
			if (property.isAnonymous()) return null;
			
			OWLObjectProperty objectProperty = property.asOWLObjectProperty();
			return ModelFactory.makeRole(objectProperty.getURI());
		}

		private void visitUnsupported() {
			m_Result = null;
		}

	}

}