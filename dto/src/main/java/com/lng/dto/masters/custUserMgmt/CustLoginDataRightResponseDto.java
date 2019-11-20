package com.lng.dto.masters.custUserMgmt;

import java.util.List;

import status.Status;

public class CustLoginDataRightResponseDto {
	
	public Status status;

	private List<CustUserBranchDto> assignedBranch;
	
	private List<CustUserBranchDto> unAssignedBranch;

	public List<CustUserBranchDto> getAssignedBranch() {
		return assignedBranch;
	}

	public void setAssignedBranch(List<CustUserBranchDto> assignedBranch) {
		this.assignedBranch = assignedBranch;
	}

	public List<CustUserBranchDto> getUnAssignedBranch() {
		return unAssignedBranch;
	}

	public void setUnAssignedBranch(List<CustUserBranchDto> unAssignedBranch) {
		this.unAssignedBranch = unAssignedBranch;
	}
}
