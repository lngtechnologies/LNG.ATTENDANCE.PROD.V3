package com.lng.dto.masters.employeeLeave;

import java.util.List;

import status.Status;

public class BranchListDto {

	private Integer custId;
	
	private Integer loginId;
	
	private List<BranchDto> branchList;
	
	public Status status;

	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}

	public List<BranchDto> getBranchList() {
		return branchList;
	}

	public void setBranchList(List<BranchDto> branchList) {
		this.branchList = branchList;
	}

	public Integer getLoginId() {
		return loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
  
}
