package edu.iastate.pdlprofiler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;



public class OntologyMaker {
	
	private static final int TOTAL_PACKAGES = 4;
	private static final double[] COVERAGE = new double[] {0.2, 0.4, 0.6, 0.8};
	private static final int QUERIES_PER_CASE = 10;
	
	private static String m_Prefix;
	private static String m_ContentFileName;
	private static List<OWLClass> m_Classes = new ArrayList<OWLClass>();
	private static int m_TotalAdditions;
	private static Random m_Ran = new Random();
	
	public static void main(String[] args) throws OWLOntologyCreationException, IOException {
		m_Prefix = args[0].substring(0, args[0].indexOf("."));
		m_ContentFileName = args[1];
		loadClasses(args[0]);
		m_TotalAdditions = (int) (m_Classes.size() * COVERAGE[TOTAL_PACKAGES - 1]) * (TOTAL_PACKAGES - 1);
		
		for (int c = 0; c < COVERAGE.length; c++) {
			List<List<OWLClass>> coveredClasses = makeCoveredClasses(COVERAGE[c]);
			for (Ontology o : makeChain(COVERAGE[c], coveredClasses)) {
				o.saveAsFile();
			}
			for (Ontology o : makeStar(COVERAGE[c], coveredClasses)) {
				o.saveAsFile();
			}
		}
		
		makeQueries();
	}

	private static void makeQueries() throws IOException {
		List<OWLClass> queryClasses = makeQueryClasses();
		final String[] TOPOLOGY = new String[] {"c", "s"};
		for (int t = 0; t < TOPOLOGY.length; t++) {
			for (int c = 0; c < COVERAGE.length; c++) {
				for (int i = 0; i < QUERIES_PER_CASE; i++) {
					String topoAndCoverage = TOPOLOGY[t] + String.valueOf((int)(COVERAGE[c] * 100));
					String name = "q_" + topoAndCoverage + "_" + i;
					Ontology query = new Ontology(name);
					String witness = m_Prefix + "_" + topoAndCoverage + "_0";
					query.addImports(witness);
					query.addSubNothingAxiom(witness, queryClasses.get(i));
					query.saveAdditionalAsFile();
				}
			}
		}
	}

	private static List<OWLClass> makeQueryClasses() {
		List<OWLClass> queryClasses = new ArrayList<OWLClass>();
		while (queryClasses.size() < QUERIES_PER_CASE) {
			OWLClass clazz = m_Classes.get(m_Ran.nextInt(m_Classes.size()));
			if (!queryClasses.contains(clazz)) {
				queryClasses.add(clazz);
			}
		}
		return queryClasses;
	}

	private static List<Ontology> makeChain(double percentage, List<List<OWLClass>> coveredClasses) {
		String prefix = m_Prefix + "_c" + String.valueOf((int)(percentage * 100)) + "_";
		final int PADDING_AXIOMS_PER_ONTOLOGY = (m_TotalAdditions - coveredClasses.get(0).size() * (TOTAL_PACKAGES - 1)) / (TOTAL_PACKAGES - 1);
		List<Ontology> ontologies = new ArrayList<Ontology>();
		for (int i = 0; i < TOTAL_PACKAGES; i++) {
			Ontology o = new Ontology(prefix + i);
			if (i < TOTAL_PACKAGES - 1) {
				o.addImports(prefix + (i + 1));
			
				List<OWLClass> thisOnt = coveredClasses.get(i);
				List<OWLClass> nextOnt = coveredClasses.get(i + 1);
				for (int c = 0; c < thisOnt.size(); c++) {
					o.addAxiom(thisOnt.get(c), prefix + (i + 1), nextOnt.get(c));
				}
				
				for (int c = 0; c < PADDING_AXIOMS_PER_ONTOLOGY; c++) {
					o.addNothingSubAxiom(m_Classes.get(c));
				}
			}
			ontologies.add(o);
		}
		return ontologies;
	}
		
	private static List<Ontology> makeStar(double percentage, List<List<OWLClass>> coveredClasses) {
		String prefix = m_Prefix + "_s" + (int)(percentage * 100) + "_";
		final int PADDING_AXIOMS = m_TotalAdditions - coveredClasses.get(0).size() * (TOTAL_PACKAGES - 1);
		List<Ontology> ontologies = new ArrayList<Ontology>();
		{
			Ontology o = new Ontology(prefix + "0");
			for (int i = 1; i < TOTAL_PACKAGES; i++) {
				o.addImports(prefix + i);
				
				List<OWLClass> thisOnt = coveredClasses.get(0);
				List<OWLClass> nextOnt = coveredClasses.get(i);
				for (int c = 0; c < thisOnt.size(); c++) {
					o.addAxiom(thisOnt.get(c), prefix + i, nextOnt.get(c));
				}
				
				for (int c = 0; c < PADDING_AXIOMS; c++) {
					o.addNothingSubAxiom(m_Classes.get(c % m_Classes.size()));
				}
			}
			ontologies.add(o);
		}
		
		for (int i = 1; i < TOTAL_PACKAGES; i++) {
			Ontology o = new Ontology(prefix + i);
			ontologies.add(o);
		}
		return ontologies;
	}

	private static List<List<OWLClass>> makeCoveredClasses(double percentage) {
		int totalCovered = (int) (m_Classes.size() * percentage);
		List<List<OWLClass>> packages = new ArrayList<List<OWLClass>>();
		for (int p = 0; p < TOTAL_PACKAGES; p++) {
			List<OWLClass> coveredClasses = new ArrayList<OWLClass>();
			while (coveredClasses.size() < totalCovered) {
				OWLClass clazz = m_Classes.get(m_Ran.nextInt(m_Classes.size()));
				if (!coveredClasses.contains(clazz)) {
					coveredClasses.add(clazz);
				}
			}
			packages.add(coveredClasses);
		}
		return packages;
	}

	private static void loadClasses(String file) throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		URI physicalURI = URIUtil.toURI(file);
		OWLOntology ontology = manager.loadOntologyFromPhysicalURI(physicalURI);
		for (OWLClass clazz : ontology.getReferencedClasses()) {
			if (clazz.isOWLThing() || clazz.isOWLNothing()
				|| !ontology.getSubClassAxiomsForRHS(clazz).isEmpty()) continue;
			
			m_Classes.add(clazz);
		}
	}
	
	static class Ontology {
		
		private String m_Name;
		private Set<String> m_Imports;
		private Set<Axiom> m_Axioms;
		
		public Ontology(String name) {
			m_Name = name;
			m_Imports = new HashSet<String>();
			m_Axioms = new HashSet<Axiom>();
		}
		
		public void addSubNothingAxiom(String subNamespace, OWLClass sub) {
			Axiom a = new Axiom(new Concept(subNamespace, sub), new Concept("owl", "Nothing"));
			m_Axioms.add(a);
		}
		
		public void addNothingSubAxiom(OWLClass sup) {
			Axiom a = new Axiom(new Concept("owl", "Nothing"), new Concept(sup));
			m_Axioms.add(a);
		}

		public void addAxiom(OWLClass sub, String supNamespace, OWLClass sup) {
			Axiom a = new Axiom(new Concept(sub), new Concept(supNamespace, sup));
			m_Axioms.add(a);
		}

		public void addImports(String other) {
			m_Imports.add(other);
		}

		private static String getFileName(String name) {
			return name + ".owl";
		}
		
		public void saveAdditionalAsFile() throws IOException {
			BufferedWriter out = new BufferedWriter(new FileWriter(getFileName(m_Name)));
			writeHeader(out);
			writeAdditional(out);
			writerFooter(out);
			out.close();
		}
		
		public void saveAsFile() throws IOException {
			BufferedWriter out = new BufferedWriter(new FileWriter(getFileName(m_Name)));
			writeHeader(out);
			writeOriginal(out);
			writeAdditional(out);
			writerFooter(out);
			out.close();
		}
		
		private void writeOriginal(BufferedWriter out) throws IOException {
			BufferedReader in = new BufferedReader(new FileReader(new File(m_ContentFileName)));
			String line;
			while ((line = in.readLine()) != null) {
				out.write(line);
				out.write("\n");
			}
			in.close();
		}
		
		private void writeAdditional(BufferedWriter out) throws IOException {
			for (Axiom axiom : m_Axioms) {
				axiom.write(out);
				out.write("\n");
			}
		}

		private void writeHeader(BufferedWriter out) throws IOException {
			out.write("<?xml version=\"1.0\"?>\n\n"); 
			out.write("<!DOCTYPE rdf:RDF [\n");
			out.write("    <!ENTITY " + m_Name + " \"" + getFileName(m_Name) + "#\" >\n");
			for (String i : m_Imports) {
				out.write("    <!ENTITY " + i + " \"" + getFileName(i) + "#\" >\n");
			}
			out.write("    <!ENTITY owl \"http://www.w3.org/2002/07/owl#\" >\n");
			out.write("    <!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" >\n");
			out.write("    <!ENTITY owl2xml \"http://www.w3.org/2006/12/owl2-xml#\" >\n");
			out.write("    <!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" >\n");
			out.write("    <!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >\n");
			out.write("]>\n");

			out.write("<rdf:RDF xmlns=\"" + getFileName(m_Name) + "#\"\n");
			out.write("     xml:base=\"" + getFileName(m_Name) + "\"\n");
			out.write("     xmlns:" + m_Name + "=\"" + getFileName(m_Name) + "#\"\n");
			for (String i : m_Imports) {
				out.write("     xmlns:" + i + "=\"" + getFileName(i) + "#\"\n");
			}
			out.write("     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n");
			out.write("     xmlns:owl2xml=\"http://www.w3.org/2006/12/owl2-xml#\"\n");
			out.write("     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n");
			out.write("     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n");
			out.write("     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n");
			if (!m_Imports.isEmpty()) {
				out.write("    <owl:Ontology rdf:about=\"\">\n");
				for (String i : m_Imports) {
					out.write("        <owl:imports rdf:resource=\"" + getFileName(i) + "\"/>\n");
				}
				out.write("    </owl:Ontology>\n");
			}
		}

		private void writerFooter(BufferedWriter out) throws IOException {
			out.write("</rdf:RDF>");
		}
		
	}
	
	static class Concept {
		
		private String m_Namespace;
		private String m_Name;
		
		public Concept(OWLClass clazz) {
			m_Name = clazz.toString();
		}

		public Concept(String namespace, OWLClass clazz) {
			this(clazz);
			m_Namespace = namespace;			
		}
		
		public Concept(String namespace, String name) {
			m_Namespace = namespace;
			m_Name = name;
		}

		@Override
		public String toString() {
			if (m_Namespace == null) {
				return "#" + m_Name;
			} else {
				return "&" + m_Namespace + ";" + m_Name;
			}
		}
		
	}
	
	static class Axiom {
		
		private Concept m_Sub;
		private Concept m_Sup;
		
		public Axiom(Concept sub, Concept sup) {
			m_Sub = sub;
			m_Sup = sup;
		}

		public void write(BufferedWriter out) throws IOException {
			out.write("    <owl:Class rdf:about=\"" + m_Sub + "\">\n"); 
			out.write("        <rdfs:subClassOf rdf:resource=\"" + m_Sup + "\"/>\n");
			out.write("    </owl:Class>\n");
		}
		
	}
	
}
