package edu.iastate.pdlreasoner.model.visitor;

import java.util.Set;

import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.ContextualizedConcept;
import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Top;
import edu.iastate.pdlreasoner.struct.MultiValuedMap;
import edu.iastate.pdlreasoner.util.CollectionUtil;

public class ExternalConceptsExtractor extends ConceptTraverser {
	
	private final PackageID m_HomePackageID;
	private MultiValuedMap<PackageID, Concept> m_ExternalConcepts;
	private Set<PackageID> m_ExternalNegations;
	
	public ExternalConceptsExtractor(PackageID homePackageID) {
		m_HomePackageID = homePackageID;
		m_ExternalConcepts = CollectionUtil.makeMultiValuedMap();
		m_ExternalNegations = CollectionUtil.makeSet();
	}
	
	public MultiValuedMap<PackageID, Concept> getExternalConcepts() {
		return m_ExternalConcepts;
	}
	
	public Set<PackageID> getExternalNegationContexts() {
		return m_ExternalNegations;
	}
	
	private void visitContextualizedAtom(ContextualizedConcept c) {
		PackageID context = c.getContext();
		if (!m_HomePackageID.equals(context)) {
			m_ExternalConcepts.add(context, c);				
		}
	}
	
	@Override
	public void visit(Top top) {
		super.visit(top);
		visitContextualizedAtom(top);
	}

	@Override
	public void visit(Atom atom) {
		super.visit(atom);
		visitContextualizedAtom(atom);
	}
	
	@Override
	public void visit(Negation negation) {
		super.visit(negation);
		PackageID context = negation.getContext();
		if (!m_HomePackageID.equals(negation.getContext())) {
			m_ExternalNegations.add(context);				
		}
	}

}