package edu.iastate.pdlprofiler;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class QueryMaker {
	
	private static final String PREFIX = "micro";
	private static final double[] COVERAGE = new double[] {0.2, 0.4, 0.6, 0.8};
	private static final String[] TOPOLOGY = new String[] {"c", "s"};
	private static final int TOTAL_PACKAGES = 4;
	private static final int QUERIES_PER_ONTOLOGY = 10;
	
	
	public static void main(String[] args) throws IOException {
		BufferedWriter cWriter = new BufferedWriter(new FileWriter("runC.bat"));
		BufferedWriter mWriter = new BufferedWriter(new FileWriter("runM.bat"));
		BufferedWriter[] sWriter = new BufferedWriter[TOTAL_PACKAGES];
		for (int i = 0; i < sWriter.length; i++) {
			sWriter[i] = new BufferedWriter(new FileWriter("runS" + i + ".bat"));
		}
		
		for (int t = 0; t < TOPOLOGY.length; t++) {
			for (int c = 0; c < COVERAGE.length; c++) {
				String caseName = TOPOLOGY[t] + (int)(COVERAGE[c] * 100);
				makeOntologyList(caseName);
				
				for (int q = 0; q < QUERIES_PER_ONTOLOGY; q++) {
					writeC(cWriter, caseName, q);
					writeM(mWriter, caseName, q);
					for (int s = 0; s < TOTAL_PACKAGES; s++) {
						writeS(sWriter[s], caseName, q, s);
					}
				}
			}
		}
		
		cWriter.close();
		mWriter.close();
		for (int i = 0; i < sWriter.length; i++) {
			sWriter[i].close();
		}
	}

	private static void makeOntologyList(String caseName) throws IOException {
		BufferedWriter lWriter = new BufferedWriter(new FileWriter(caseName + ".txt"));
		for (int p = 0; p < TOTAL_PACKAGES; p++) {
			lWriter.write(PREFIX + "_" + caseName + "_" + p + ".owl");
			lWriter.write("\n");
		}		
		lWriter.close();
	}

	private static final String BASE = "java -Xms1024m -Xmx1024m -jar pdlreasoner.jar -t ";
	
	private static String getQueryOntology(String caseName, int q) {
		return "q_" + caseName + "_" + q + ".owl";
	}

	private static String getWitness(String caseName) {
		return getPackageName(caseName, 0);
	}
	
	private static String getPackageName(String caseName, int s) {
		return PREFIX + "_" + caseName + "_" + s + ".owl";
	}

	private static void writeC(BufferedWriter writer, String caseName, int q) throws IOException {
		String line = BASE + "-c ";
		line += caseName + ".txt ";
		line += getQueryOntology(caseName, q) + " ";
		line += getWitness(caseName) + " ";
		line += ">> C.txt\n";
		writer.write(line);
	}
		
	private static void writeM(BufferedWriter writer, String caseName, int q) throws IOException {
		String line = BASE + "-m " + TOTAL_PACKAGES + " ";
		line += getQueryOntology(caseName, q) + " ";
		line += getWitness(caseName) + " ";
		line += ">> M.txt\n";
		writer.write(line);
	}
	
	private static void writeS(BufferedWriter writer, String caseName, int q, int s) throws IOException {
		String line = BASE + "-s ";
		line += getPackageName(caseName, s) + " ";
		line += getQueryOntology(caseName, q) + " ";
		line += getWitness(caseName) + " ";
		line += ">> S" + s + ".txt\n";
		writer.write(line);
	}
	
}
