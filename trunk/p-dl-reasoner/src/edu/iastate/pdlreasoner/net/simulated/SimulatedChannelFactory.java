package edu.iastate.pdlreasoner.net.simulated;

import java.io.File;
import java.net.URL;

import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.ChannelFactory;
import org.w3c.dom.Element;

public class SimulatedChannelFactory implements ChannelFactory {

	@Override
	public Channel createChannel() throws ChannelException {
		return null;
	}

	@Override
	public Channel createChannel(Object arg0) throws ChannelException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Channel createChannel(String arg0) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Channel createMultiplexerChannel(String arg0, String arg1) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Channel createMultiplexerChannel(String arg0, String arg1, boolean arg2, String arg3) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMultiplexerConfig(Object arg0) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMultiplexerConfig(File arg0) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMultiplexerConfig(Element arg0) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMultiplexerConfig(URL arg0) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMultiplexerConfig(String arg0) throws Exception {
		throw new UnsupportedOperationException();
	}

}
