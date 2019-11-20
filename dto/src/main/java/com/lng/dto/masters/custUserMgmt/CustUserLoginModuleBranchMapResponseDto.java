package com.lng.dto.masters.custUserMgmt;

import java.util.List;

import status.Status;

public class CustUserLoginModuleBranchMapResponseDto {

	private Integer custId;
	
	private List<CustUserLoginDto> loginDetails;
	
	// private List<CustUserModulesDto> modules;
	
	// private List<CustUserBranchesDto> branches;
	
	public Status status;

	public List<CustUserLoginDto> getLoginDetails() {
		return loginDetails;
	}

	public void setLoginDetails(List<CustUserLoginDto> loginDetails) {
		this.loginDetails = loginDetails;
	}

	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}

}
