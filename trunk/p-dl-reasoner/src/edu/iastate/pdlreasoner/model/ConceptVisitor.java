package edu.iastate.pdlreasoner.model;

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
