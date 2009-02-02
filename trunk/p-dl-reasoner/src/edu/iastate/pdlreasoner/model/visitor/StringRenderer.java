package edu.iastate.pdlreasoner.model.visitor;

import edu.iastate.pdlreasoner.model.AllValues;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.Restriction;
import edu.iastate.pdlreasoner.model.SetOp;
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
			.append(atom.getFragment());
	}

	@Override
	public void visit(Negation negation) {
		m_Builder.append(NOT)
			.append("(")
			.append(negation.getContext())
			.append(",");
		
		negation.getNegatedConcept().accept(this);
		
		m_Builder.append(")");
	}

	private void visitSetOp(SetOp set) {
		m_Builder.append("(");

		Concept[] operands = set.getOperands();
		operands[0].accept(this);
		for (int i = 1; i < operands.length; i++) {
			m_Builder.append(",");
			operands[i].accept(this);
		}
					
		m_Builder.append(")");
	}

	@Override
	public void visit(And and) {
		m_Builder.append(AND);
		visitSetOp(and);
	}

	@Override
	public void visit(Or or) {
		m_Builder.append(OR);
		visitSetOp(or);
	}
	
	private void visitRestriction(Restriction r) {
		m_Builder.append("(")
			.append(r.getRole())
			.append(",");
	
		r.getFiller().accept(this);
		
		m_Builder.append(")");
	}

	@Override
	public void visit(SomeValues someValues) {
		m_Builder.append(SOME);
		visitRestriction(someValues);		
	}

	@Override
	public void visit(AllValues allValues) {
		m_Builder.append(ALL);
		visitRestriction(allValues);
	}

}
