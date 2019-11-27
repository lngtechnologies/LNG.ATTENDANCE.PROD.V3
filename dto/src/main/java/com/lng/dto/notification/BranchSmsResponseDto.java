package com.lng.dto.notification;

import java.util.List;

import status.Status;

public class BranchSmsResponseDto {

	private List<BranchDto> branchDtoList;
	
	public Status status;

	public List<BranchDto> getBranchDtoList() {
		return branchDtoList;
	}

	public void setBranchDtoList(List<BranchDto> branchDtoList) {
		this.branchDtoList = branchDtoList;
	}

}
