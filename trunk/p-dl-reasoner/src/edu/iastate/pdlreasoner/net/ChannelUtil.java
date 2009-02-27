package edu.iastate.pdlreasoner.net;

public class ChannelUtil {

	private static String Name = "PDL";

	public static void setSessionName(String name) {
		Name = name;
	}
	
	public static String getSessionName() {
		return Name;
	}
	
}
