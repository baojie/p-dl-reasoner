package edu.iastate.pdlreasoner.model;

import java.net.URI;

public class ModelFactory {

	public DLPackage makePackage(URI uri) {
		return new DLPackage(uri);
	}
	
	public Top makeTop(DLPackage homePackage) {
		return new Top(homePackage);
	}
	
	public Atom makeAtom(DLPackage homePackage, URI uri) {
		return new Atom(homePackage, uri);
	}
	
	public Role makeRole(URI uri) {
		return new Role(uri);
	}
	
	public Not makeNot(DLPackage context, Concept concept) {
		return new Not(context, concept);
	}
	
	public And makeAnd(Concept[] concepts) {
		return new And(concepts);
	}
	
	public Or makeOr(Concept[] concepts) {
		return new Or(concepts);
	}
	
	public SomeValues makeSomeValues(Role role, Concept concept) {
		return new SomeValues(role, concept);
	}

	public AllValues makeAllValues(Role role, Concept concept) {
		return new AllValues(role, concept);
	}
}
