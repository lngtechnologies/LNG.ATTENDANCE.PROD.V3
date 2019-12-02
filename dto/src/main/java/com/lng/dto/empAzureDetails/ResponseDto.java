package com.lng.dto.empAzureDetails;

import java.util.List;

import status.Status;

public class ResponseDto {

	private List<RegisteredEmployeeDetailsDto> empDetails;
	
	public Status status;

	public List<RegisteredEmployeeDetailsDto> getEmpDetails() {
		return empDetails;
	}

	public void setEmpDetails(List<RegisteredEmployeeDetailsDto> empDetails) {
		this.empDetails = empDetails;
	}
	
}
