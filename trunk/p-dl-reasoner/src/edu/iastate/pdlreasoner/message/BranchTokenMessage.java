package edu.iastate.pdlreasoner.message;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import edu.iastate.pdlreasoner.model.PackageID;
import edu.iastate.pdlreasoner.tableau.branch.BranchToken;

public class BranchTokenMessage implements MessageToSlave, MessageToMaster {

	private static final long serialVersionUID = 1L;
	
	private PackageID m_PackageID;
	private BranchToken m_Token;
	
	public BranchTokenMessage(PackageID packageID, BranchToken token) {
		m_PackageID = packageID;
		m_Token = token;
	}

	public PackageID getPackageID() {
		return m_PackageID;
	}
	
	public BranchToken getToken() {
		return m_Token;
	}

	@Override
	public void execute(TableauSlaveMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}

	@Override
	public void execute(TableauMasterMessageProcessor messageProcessor) {
		messageProcessor.process(this);
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
