package edu.iastate.pdlreasoner.kb.owlapi;

import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLObjectIntersectionOf;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.util.OWLAxiomVisitorAdapter;

import edu.iastate.pdlreasoner.exception.OWLDescriptionNotSupportedException;
import edu.iastate.pdlreasoner.kb.OntologyPackage;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class OntologyConverter {
	
	private static final Logger LOGGER = Logger.getLogger(OntologyConverter.class);

	private OntologyPackageIDStore m_PackageIDStore;
	private ConceptConverter m_ConceptConverter;
	
	public OntologyConverter(OntologyPackageIDStore packageIDStore, ConceptConverter conceptConverter) {
		m_PackageIDStore = packageIDStore;
		m_ConceptConverter = conceptConverter;
	}

	public OntologyPackage convert(OWLOntology ontology) throws OWLDescriptionNotSupportedException {
		PackageID packageID = m_PackageIDStore.getPackageID(ontology.getURI());
		m_ConceptConverter.setPackageID(packageID);
	
		OntologyPackage ontologyPackage = new OntologyPackage(packageID);
		AxiomConverter axiomConverter = new AxiomConverter(ontologyPackage);
		for (OWLAxiom axiom : ontology.getAxioms()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Converting axiom: " + axiom);
			}
			
			axiom.accept(axiomConverter);
		}
		
		ontologyPackage.init();
		
		return ontologyPackage;
	}

	private class AxiomConverter extends OWLAxiomVisitorAdapter {

		private OntologyPackage m_OntologyPackage;

		public AxiomConverter(OntologyPackage ontologyPackage) {
			m_OntologyPackage = ontologyPackage;
		}

		@Override
		public void visit(OWLSubClassAxiom axiom) {
			OWLDescription owlSub = axiom.getSubClass();
			OWLDescription owlSup = axiom.getSuperClass();
			visitSubClassAxiom(owlSub, owlSup);
		}

		@Override
		public void visit(OWLEquivalentClassesAxiom axiom) {
			List<OWLDescription> axioms = CollectionUtil.makeList(axiom.getDescriptions());
			for (int i = 0; i < axioms.size() - 1; i++) {
				OWLDescription desc1 = axioms.get(i);
				OWLDescription desc2 = axioms.get(i + 1);
				visitSubClassAxiom(desc1, desc2);
				visitSubClassAxiom(desc2, desc1);
			}
		}
		
		@Override
		public void visit(OWLDisjointClassesAxiom axiom) {
			List<OWLDescription> axioms = CollectionUtil.makeList(axiom.getDescriptions());
			for (int i = 0; i < axioms.size() - 1; i++) {
				for (int j = i + 1; j < axioms.size(); j++) {
					OWLDescription desc1 = axioms.get(i);
					OWLDescription desc2 = axioms.get(j);
					visitDisjointClassAxiom(desc1, desc2);
				}
			}
		}

		private void visitSubClassAxiom(OWLDescription sub, OWLDescription sup) {
			if (sup.isOWLThing()) return;
			
			try {
				Concept subC = m_ConceptConverter.convert(sub);
				Concept supC = m_ConceptConverter.convert(sup);
				m_OntologyPackage.addAxiom(subC, supC);
			} catch (OWLDescriptionNotSupportedException e) {
				e.printStackTrace();
			}
		}
		
		private void visitDisjointClassAxiom(OWLDescription desc1, OWLDescription desc2) {
			OWLObjectIntersectionOf intersect = OWLUtil.Factory.getOWLObjectIntersectionOf(desc1, desc2);
			visitSubClassAxiom(intersect, OWLUtil.Factory.getOWLNothing());
		}

	}
	
}
