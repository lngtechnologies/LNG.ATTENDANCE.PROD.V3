package com.lng.dto.masters.custUserMgmt;

import java.util.List;

import status.Status;

public class CompanyUserLoginModuleMapResponseDto {
	
	private Integer custId;
	
    private Integer loginId;
	
	private List<CompanyUserLoginDto> loginDetails;
	
	public Status status;

	public Integer getLoginId() {
		return loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}

	public List<CompanyUserLoginDto> getLoginDetails() {
		return loginDetails;
	}

	public void setLoginDetails(List<CompanyUserLoginDto> loginDetails) {
		this.loginDetails = loginDetails;
	}

	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}
	
	
	
}
