package com.lng.dto.masters.employeeLeave;

import java.util.List;

import status.Status;

public class EmployeeDtatListDto {

	private Integer brId;
	
	private List<EmployeeDto> employeeDtoList;
	
	public Status status;

	public Integer getBrId() {
		return brId;
	}

	public void setBrId(Integer brId) {
		this.brId = brId;
	}

	public List<EmployeeDto> getEmployeeDtoList() {
		return employeeDtoList;
	}

	public void setEmployeeDtoList(List<EmployeeDto> employeeDtoList) {
		this.employeeDtoList = employeeDtoList;
	}

}
