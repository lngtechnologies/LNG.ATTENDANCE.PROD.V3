package com.lng.dto.masters.custUserMgmt;

import java.util.List;

import status.Status;

public class CustUserLoginModuleBranchDto {

	private CustUserMgmtDto userDetails;
	
	private List<CustUserModuleDto> modules;
	
	private List<CustUserBranchDto> branches;
	
	public Status status;

	public CustUserMgmtDto getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(CustUserMgmtDto userDetails) {
		this.userDetails = userDetails;
	}

	public List<CustUserModuleDto> getModules() {
		return modules;
	}

	public void setModules(List<CustUserModuleDto> modules) {
		this.modules = modules;
	}

	public List<CustUserBranchDto> getBranches() {
		return branches;
	}

	public void setBranches(List<CustUserBranchDto> branches) {
		this.branches = branches;
	}

}
