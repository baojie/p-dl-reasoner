package edu.iastate.pdlreasoner.net.simulated;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.jgroups.Address;

public class SimulatedAddress implements Address {

	@Override
	public boolean isMulticastAddress() {
		return false;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public int compareTo(Address o) {
		return hashCode() - o.hashCode();
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void readFrom(DataInputStream in) throws IOException, IllegalAccessException, InstantiationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeTo(DataOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

}
