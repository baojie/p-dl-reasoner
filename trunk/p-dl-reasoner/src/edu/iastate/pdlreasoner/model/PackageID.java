package edu.iastate.pdlreasoner.model;

import java.io.Serializable;
import java.net.URI;

import edu.iastate.pdlreasoner.util.URIUtil;

public class PackageID implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private URI m_URI;
	
	private String m_ShortURI;

	protected PackageID(URI uri) {
		m_URI = uri;
		m_ShortURI = URIUtil.getLastOfPath(uri);
	}
	
	public URI getURI() {
		return m_URI;
	}
	
	public String toLongString() {
		return m_URI.toString();
	}
	
	public String toStringWithBracket() {
		return "<" + m_ShortURI + "> ";
	}
	
	@Override
	public String toString() {
		return m_ShortURI;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PackageID)) return false;
		PackageID other = (PackageID) obj;
		return m_URI.equals(other.m_URI);
	}
	
	@Override
	public int hashCode() {
		return m_URI.hashCode();
	}
	
}
