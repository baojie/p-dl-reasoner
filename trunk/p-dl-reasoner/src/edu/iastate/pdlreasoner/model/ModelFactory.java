package edu.iastate.pdlreasoner.model;

import java.net.URI;

public class ModelFactory {

	public static DLPackage makePackage(URI uri) {
		return new DLPackage(uri);
	}
	
	public static Top makeTop(DLPackage homePackage) {
		return new Top(homePackage);
	}
	
	public static Atom makeAtom(DLPackage homePackage, URI uri) {
		return new Atom(homePackage, uri);
	}
	
	public static Role makeRole(URI uri) {
		return new Role(uri);
	}
	
	public static Not makeNot(DLPackage context, Concept concept) {
		return new Not(context, concept);
	}
	
	public static And makeAnd(Concept[] concepts) {
		return new And(concepts);
	}
	
	public static Or makeOr(Concept[] concepts) {
		return new Or(concepts);
	}
	
	public static SomeValues makeSomeValues(Role role, Concept concept) {
		return new SomeValues(role, concept);
	}

	public static AllValues makeAllValues(Role role, Concept concept) {
		return new AllValues(role, concept);
	}
	
}
