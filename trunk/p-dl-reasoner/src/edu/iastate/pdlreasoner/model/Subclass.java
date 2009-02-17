package edu.iastate.pdlreasoner.model;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Subclass {

	private Concept m_Sub;
	private Concept m_Sup;

	protected Subclass(Concept sub, Concept sup) {
		m_Sub = sub;
		m_Sup = sup;
	}

	public Concept getSub() {
		return m_Sub;
	}
	
	public Concept getSup() {
		return m_Sup;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
