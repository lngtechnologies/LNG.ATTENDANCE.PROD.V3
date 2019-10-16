package com.lng.dto.employeeType;

import java.util.List;

import status.Status;

public class EmployeeTypeListResponseDto {
	
	private List<EmployeeTypeDto> employeeTypeDtoList;
	
	public Status status;

	public List<EmployeeTypeDto> getEmployeeTypeDtoList() {
		return employeeTypeDtoList;
	}

	public void setEmployeeTypeDtoList(List<EmployeeTypeDto> employeeTypeDtoList) {
		this.employeeTypeDtoList = employeeTypeDtoList;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	

}
