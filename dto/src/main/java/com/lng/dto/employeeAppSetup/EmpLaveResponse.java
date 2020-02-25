package com.lng.dto.employeeAppSetup;

import java.util.List;

import status.Status;

public class EmpLaveResponse {


	private List<EmpLeavesDto> empLeaveDtoList;

	public Status status;

	public List<EmpLeavesDto> getEmpLeaveDtoList() {
		return empLeaveDtoList;
	}

	public void setEmpLeaveDtoList(List<EmpLeavesDto> empLeaveDtoList) {
		this.empLeaveDtoList = empLeaveDtoList;
	}

}
