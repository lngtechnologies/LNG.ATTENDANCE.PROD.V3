package com.lng.dto.masters.custUserMgmt;

import java.util.List;

import status.Status;

public class CompanyUserLoginModuleDto {
	
	private CustUserMgmtDto userDetails;
	
	private List<CustUserModuleDto> modules;
	
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
