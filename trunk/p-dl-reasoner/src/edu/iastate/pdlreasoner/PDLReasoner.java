package edu.iastate.pdlreasoner;

import org.jgroups.ChannelException;
import org.jgroups.JChannel;

public class PDLReasoner {

	public static void main(String[] args) throws ChannelException {
		JChannel channel = new JChannel();
		channel.connect("Test");
		channel.close();
	}

}
