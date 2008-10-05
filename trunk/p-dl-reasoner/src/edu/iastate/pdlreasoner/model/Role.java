package edu.iastate.pdlreasoner.model;

import java.net.URI;

public class Role {
	
	protected URI m_URI;
	
	protected Role(URI uri) {
		m_URI = uri;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Role)) return false;
		Role other = (Role) obj;
		return m_URI.equals(other.m_URI);
	}
	
	@Override
	public int hashCode() {
		return m_URI.hashCode();
	}
	
}
