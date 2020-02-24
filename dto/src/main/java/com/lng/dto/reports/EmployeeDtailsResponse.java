package com.lng.dto.reports;

import java.util.List;

import status.Status;

public class EmployeeDtailsResponse {
	
	public List<EmployeeDetailsDto> employeeDetails;

	public Status status;

	public List<EmployeeDetailsDto> getEmployeeDetails() {
		return employeeDetails;
	}

	public void setEmployeeDetails(List<EmployeeDetailsDto> employeeDetails) {
		this.employeeDetails = employeeDetails;
	}
	
	
	

}
