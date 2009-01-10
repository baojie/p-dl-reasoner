package edu.iastate.pdlreasoner.model.visitor;

import edu.iastate.pdlreasoner.model.AllValues;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.SomeValues;
import edu.iastate.pdlreasoner.model.Top;

public interface ConceptVisitor {

	void visit(Bottom bottom);
	void visit(Top top);
	void visit(Atom atom);
	void visit(Negation negation);
	void visit(And and);
	void visit(Or or);
	void visit(SomeValues someValues);
	void visit(AllValues allValues);

}
