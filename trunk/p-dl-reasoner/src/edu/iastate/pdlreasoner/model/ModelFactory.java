package edu.iastate.pdlreasoner.model;

import java.net.URI;

public class ModelFactory {

	public static PackageID makePackageID(URI uri) {
		return new PackageID(uri);
	}
	
	public static Top makeTop(PackageID homePackageID) {
		return new Top(homePackageID);
	}
	
	public static Atom makeAtom(PackageID homePackageID, String fragment) {
		return new Atom(homePackageID, fragment);
	}
	
	public static Role makeRole(URI uri) {
		return new Role(uri);
	}
	
	public static Negation makeNegation(PackageID context, Concept negatedConcept) {
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
