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
	
	public static Negation makeNegation(DLPackage context, Concept negatedConcept) {
		return new Negation(context, negatedConcept);
	}
	
	public static And makeAnd(Concept... operands) {
		return new And(operands);
	}
	
	public static Or makeOr(Concept... operands) {
		return new Or(operands);
	}
	
	public static SomeValues makeSomeValues(Role role, Concept filler) {
		return new SomeValues(role, filler);
	}

	public static AllValues makeAllValues(Role role, Concept filler) {
		return new AllValues(role, filler);
	}

	public static Subclass makeSub(Concept sub, Concept sup) {
		return new Subclass(sub, sup);
	}
	
}
