package edu.iastate.pdlreasoner.net.simulated;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelClosedException;
import org.jgroups.ChannelException;
import org.jgroups.ChannelNotConnectedException;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.TimeoutException;
import org.jgroups.View;
import org.jgroups.stack.ProtocolStack;

public class SimulatedChannel extends Channel {
	
	@Override
	public void setReceiver(Receiver r) {
		//
	}

	@Override
	public void send(Message msg) throws ChannelNotConnectedException, ChannelClosedException {
		//
	}

	@Override
	public void send(Address dst, Address src, Serializable obj) throws ChannelNotConnectedException, ChannelClosedException {
		//
	}

	@Override
	public void connect(String clusterName) throws ChannelException {
		//
	}

	@Override
	public Address getLocalAddress() {
		//
		return null;
	}

	@Override
	public View getView() {
		//
		return null;
	}

	@Override
	public void disconnect() {
	}

	@Override
	public void close() {
	}

	@Override
	public void blockOk() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> dumpStats() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean flushSupported() {
		return false;
	}

	@Override
	public void connect(String arg0, Address arg1, String arg2, long arg3) throws ChannelException {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean getAllStates(Vector arg0, long arg1) throws ChannelNotConnectedException, ChannelClosedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getChannelName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getClusterName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> getInfo() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Log getLog() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getOpt(int arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ProtocolStack getProtocolStack() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getState(Address arg0, long arg1) throws ChannelNotConnectedException, ChannelClosedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getState(Address arg0, String arg1, long arg2) throws ChannelNotConnectedException, ChannelClosedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isConnected() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isOpen() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object peek(long arg0) throws ChannelNotConnectedException, ChannelClosedException, TimeoutException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object receive(long arg0) throws ChannelNotConnectedException, ChannelClosedException, TimeoutException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void returnState(byte[] arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void returnState(byte[] arg0, String arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setInfo(String arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setOpt(int arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void shutdown() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean startFlush(boolean arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean startFlush(List<Address> arg0, boolean arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean startFlush(long arg0, boolean arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void stopFlush() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void stopFlush(List<Address> arg0) {
		throw new UnsupportedOperationException();
	}

}
