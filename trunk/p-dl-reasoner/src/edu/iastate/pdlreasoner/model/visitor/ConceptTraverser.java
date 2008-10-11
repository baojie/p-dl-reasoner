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

public class ConceptTraverser implements ConceptVisitor {

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
		negation.getNegatedConcept().accept(this);
	}

	private void visitSetOp(SetOp set) {
		for (Concept operand : set.getOperands()) {
			operand.accept(this);
		}
	}
	
	@Override
	public void visit(And and) {
		visitSetOp(and);
	}

	@Override
	public void visit(Or or) {
		visitSetOp(or);
	}

	private void visitRestriction(Restriction r) {
		r.getFiller().accept(this);
	}
	
	@Override
	public void visit(SomeValues someValues) {
		visitRestriction(someValues);
	}

	@Override
	public void visit(AllValues allValues) {
		visitRestriction(allValues);
	}

}