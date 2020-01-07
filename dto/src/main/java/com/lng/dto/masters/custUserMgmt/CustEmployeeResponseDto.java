package com.lng.dto.masters.custUserMgmt;

import java.util.List;

import status.Status;

public class CustEmployeeResponseDto {

	private List<CustEmployeeDto> empData;
	
	public Status status;

	public List<CustEmployeeDto> getEmpData() {
		return empData;
	}

	public void setEmpData(List<CustEmployeeDto> empData) {
		this.empData = empData;
	}
}
