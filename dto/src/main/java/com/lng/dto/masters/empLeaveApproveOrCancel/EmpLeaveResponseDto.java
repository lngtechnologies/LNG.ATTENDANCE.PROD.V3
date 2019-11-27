package com.lng.dto.masters.empLeaveApproveOrCancel;

import java.util.List;

import status.Status;

public class EmpLeaveResponseDto {

	private List<EmpLeaveDto> empLeaveDtoList;
	
	public Status status;

	public List<EmpLeaveDto> getEmpLeaveDtoList() {
		return empLeaveDtoList;
	}

	public void setEmpLeaveDtoList(List<EmpLeaveDto> empLeaveDtoList) {
		this.empLeaveDtoList = empLeaveDtoList;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
