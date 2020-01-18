package com.lng.dto.masters.custUserMgmt;

import java.util.List;

import status.Status;

public class CustUserBranchResDto {

	private List<CustUserBranchDto> branches;
	
	public Status status;

	public List<CustUserBranchDto> getBranches() {
		return branches;
	}

	public void setBranches(List<CustUserBranchDto> branches) {
		this.branches = branches;
	}
}
