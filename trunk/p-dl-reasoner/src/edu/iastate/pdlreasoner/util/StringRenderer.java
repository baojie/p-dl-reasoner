package edu.iastate.pdlreasoner.util;

import edu.iastate.pdlreasoner.model.AllValues;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.ConceptVisitor;
import edu.iastate.pdlreasoner.model.Not;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.SomeValues;
import edu.iastate.pdlreasoner.model.Top;

public class StringRenderer implements ConceptVisitor {
	
	private static final String BOTTOM = "_BOTTOM_";
	private static final String TOP = "(_TOP_)";
	private static final String NOT = "Not";
	private static final String AND = "And";
	private static final String OR = "Or";
	private static final String ALL = "All";
	private static final String SOME = "Some";
	
	private StringBuilder m_Builder;
	
	public String render(Concept c) {
		m_Builder = new StringBuilder();
		c.accept(this);
		return m_Builder.toString();
	}

	@Override
	public void visit(Bottom bottom) {
		m_Builder.append(BOTTOM);
	}

	@Override
	public void visit(Top top) {
		m_Builder.append(top.getContext())
			.append(TOP);
	}

	@Override
	public void visit(Atom atom) {
		m_Builder.append(atom.getContext())
			.append("(")
			.append(atom.getURI())
			.append(")");
	}

	@Override
	public void visit(Not not) {
		m_Builder.append(NOT)
			.append("(")
			.append(not.getContext())
			.append(", ");
		
		not.getNegatedConcept().accept(this);
		
		m_Builder.append(")");
	}

	@Override
	public void visit(And and) {
	}

	@Override
	public void visit(Or or) {
	}

	@Override
	public void visit(SomeValues someValues) {
		m_Builder.append(SOME)
			.append("(")
			.append(someValues.getRole())
			.append(", ");
	
		someValues.getFiller().accept(this);
		
		m_Builder.append(")");
	}

	@Override
	public void visit(AllValues allValues) {
	}

}
