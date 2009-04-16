package edu.iastate.pdlprofiler.result;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class ResultProcessor {
	
	private static final String CENT = "Centralized";
	private static final String DIST = "Distributed";
	private static final String MAST = "Master";
	private static final String SLAV = "Slave";
	
	private static int Slaves;
	private static int QueriesPerCase;
	private static List<String> Configs = new ArrayList<String>();
	private static List<Integer> Coverages = new ArrayList<Integer>();
	
	private static List<RunRecord> Averages = new ArrayList<RunRecord>();;
	
	public static void main(String[] args) throws IOException {
		Slaves = Integer.parseInt(args[0]);
		QueriesPerCase = Integer.parseInt(args[1]);
		
		BufferedReader cReader = new BufferedReader(new FileReader(new File("C.txt")));
		BufferedReader mReader = new BufferedReader(new FileReader(new File("M.txt")));
		BufferedReader[] sReader = new BufferedReader[Slaves];
		for (int i = 0; i < Slaves; i++) {
			sReader[i] = new BufferedReader(new FileReader(new File("S" + i + ".txt")));
		}
		
		String cLine;
		String mLine;
		String[] sLine = new String[Slaves];
		while ((cLine = cReader.readLine()) != null) {
			if (cLine.trim().isEmpty()) break;
			
			RunID id = parseRunID(cLine);
			
			RunRecord[] runs = new RunRecord[QueriesPerCase];
			
			mLine = mReader.readLine();
			for (int i = 0; i < Slaves; i++) {
				sLine[i] = sReader[i].readLine();
			}
			
			runs[0] = RunRecord.parse(cLine, mLine, sLine);
			
			for (int r = 1; r < runs.length; r++) {
				cLine = cReader.readLine();
				mLine = mReader.readLine();
				for (int i = 0; i < Slaves; i++) {
					sLine[i] = sReader[i].readLine();
				}
				
				runs[r] = RunRecord.parse(cLine, mLine, sLine);
			}
			
			RunRecord average = computeAverage(runs);
			average.setID(id);
			Averages.add(average);
		}
		
		for (int i = 0; i < Slaves; i++) {
			sReader[i].close();
		}
		mReader.close();
		cReader.close();			
		
		printReport();
	}

	private static RunID parseRunID(String line) {
		String[] splits = line.split(",");
		String queryName = splits[0];
		
		String config = String.valueOf(queryName.charAt(2));
		int coverage = Integer.parseInt(queryName.substring(3, 5));
		
		if (!Configs.contains(config)) {
			Configs.add(config);
		}
		if (!Coverages.contains(coverage)) {
			Coverages.add(coverage);
		}
		
		return new RunID(config, coverage);
	}

	private static RunRecord computeAverage(RunRecord[] runs) {
		RunRecord average = runs[0].copy();
		for (int i = 1; i < runs.length; i++) {
			average.add(runs[i]);
		}
		average.divideBy(runs.length);
		return average;
	}

	private static void printReport() throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(new File("report1.csv")));
		
		printHeader(out);
		printRunRecord(out, "Clashes",
			new RunRecordValueGetter() {
				public double get(RunRecord r) {
					return r.getCentralizedClashes();
				}
			},
			new RunRecordValueGetter() {
				public double get(RunRecord r) {
					return r.getDistributedClashes();
				}
			});
		printRunRecord(out, "Messages",
				new RunRecordValueGetter() {
					public double get(RunRecord r) {
						return r.getCentralizedMessages();
					}
				},
				new RunRecordValueGetter() {
					public double get(RunRecord r) {
						return r.getDistributedMessages();
					}
				});
		printRunRecord(out, "Response Times",
				new RunRecordValueGetter() {
					public double get(RunRecord r) {
						return r.getCentralizedResponseTime();
					}
				},
				new RunRecordValueGetter() {
					public double get(RunRecord r) {
						return r.getDistributedResponseTime();
					}
				});
		printAllRecord(out, "Reason+Message Times",
				new RecordValueGetter() {
					public double get(Record r) {
						return r.getReasonMessageTime();
					}
				});
		printAllRecord(out, "Reason Times",
				new RecordValueGetter() {
					public double get(Record r) {
						return r.m_ReasonTime;
					}
				});
		printMasterSlaveRecord(out, "Messaging Times",
				new RecordValueGetter() {
					public double get(Record r) {
						return r.getMessageTime();
					}
				});
		
		out.close();		
	}

	private static void printHeader(BufferedWriter out) throws IOException {
		out.write(",,,Coverage\n");
		out.write(",,,");
		for (int c : Coverages) {
			out.write(c + ",");
		}
		out.write("\n");
	}

	private static RunRecord find(String config, int coverage) {
		RunID id = new RunID(config, coverage);
		for (RunRecord r : Averages) {
			if (r.m_ID.equals(id)) {
				return r;
			}
		}
		return null;
	}

	private static void printRunRecord(BufferedWriter out, String title, RunRecordValueGetter cent, RunRecordValueGetter dist) throws IOException {
		for (int i = 0; i < Configs.size(); i++) {
			if (i == 0) out.write(title);
			out.write(",");
			out.write(Configs.get(i) + ",");
			out.write(CENT + ",");
			for (int j = 0; j < Coverages.size(); j++) {
				double v = cent.get(find(Configs.get(i), Coverages.get(j)));
				out.write(v + ",");
			}
			out.write("\n");

			out.write(",," + DIST + ",");
			for (int j = 0; j < Coverages.size(); j++) {
				double v = dist.get(find(Configs.get(i), Coverages.get(j)));
				out.write(v + ",");
			}
			out.write("\n");
		}
	}

	private static void printAllRecord(BufferedWriter out, String title, RecordValueGetter getter) throws IOException {
		for (int i = 0; i < Configs.size(); i++) {
			if (i == 0) out.write(title);
			out.write(",");
			out.write(Configs.get(i) + ",");
			out.write(CENT + ",");
			for (int j = 0; j < Coverages.size(); j++) {
				double v = getter.get(find(Configs.get(i), Coverages.get(j)).m_Central);
				out.write(v + ",");
			}
			out.write("\n");
			
			out.write(",," + MAST + ",");
			for (int j = 0; j < Coverages.size(); j++) {
				double v = getter.get(find(Configs.get(i), Coverages.get(j)).m_Master);
				out.write(v + ",");
			}
			out.write("\n");
			
			for (int s = 0; s < Slaves; s++) {
				out.write(",," + SLAV + s + ",");
				for (int j = 0; j < Coverages.size(); j++) {
					double v = getter.get(find(Configs.get(i), Coverages.get(j)).m_Slaves[s]);
					out.write(v + ",");
				}
				out.write("\n");
			}
		}
	}

	private static void printMasterSlaveRecord(BufferedWriter out, String title, RecordValueGetter getter) throws IOException {
		for (int i = 0; i < Configs.size(); i++) {
			if (i == 0) out.write(title);
			out.write(",");
			out.write(Configs.get(i) + ",");
			
			out.write(MAST + ",");
			for (int j = 0; j < Coverages.size(); j++) {
				double v = getter.get(find(Configs.get(i), Coverages.get(j)).m_Master);
				out.write(v + ",");
			}
			out.write("\n");
			
			for (int s = 0; s < Slaves; s++) {
				out.write(",," + SLAV + s + ",");
				for (int j = 0; j < Coverages.size(); j++) {
					double v = getter.get(find(Configs.get(i), Coverages.get(j)).m_Slaves[s]);
					out.write(v + ",");
				}
				out.write("\n");
			}
		}
	}

	interface RunRecordValueGetter {
		double get(RunRecord r);
	}
	
	interface RecordValueGetter {
		double get(Record r);
	}
	
}
