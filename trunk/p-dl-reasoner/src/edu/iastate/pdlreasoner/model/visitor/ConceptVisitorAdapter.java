package edu.iastate.pdlreasoner.model.visitor;

import edu.iastate.pdlreasoner.model.AllValues;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.SomeValues;
import edu.iastate.pdlreasoner.model.Top;

public class ConceptVisitorAdapter implements ConceptVisitor {

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
