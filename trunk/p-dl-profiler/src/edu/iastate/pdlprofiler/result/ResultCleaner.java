package edu.iastate.pdlprofiler.result;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResultCleaner {
	
	public static void main(String[] args) throws IOException {
		int SLAVES = Integer.parseInt(args[0]);
		List<String> files = new ArrayList<String>();
		files.add("M.txt");
		for (int i = 0; i < SLAVES; i++) {
			files.add("S" + i + ".txt");
		}
		
		for (String file : files) {
			cleanFile(file);
		}
	}

	private static void cleanFile(String filename) throws IOException {
		File file = new File(filename);
		File tmp = new File(file + ".tmp");
		BufferedReader in = new BufferedReader(new FileReader(file));
		BufferedWriter out = new BufferedWriter(new FileWriter(tmp));
		
		String line;
		while ((line = in.readLine()) != null) {
			if (line.trim().isEmpty() ||
				line.startsWith("---") ||
				line.startsWith("GMS")) {
				continue;
			}
			out.write(line);
			out.write("\n");
		}
		
		out.close();
		in.close();
		
		file.delete();
		tmp.renameTo(file);
	}
	
}
