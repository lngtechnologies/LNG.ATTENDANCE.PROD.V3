package com.lng.attendancecustomerservice.entity.reports;

import java.util.List;

import status.Status;

public class EmpOfficeOutResponse {

	private List<EmpOfficeOutDto> officeOutDetails;
	
	public Status status;

	public List<EmpOfficeOutDto> getOfficeOutDetails() {
		return officeOutDetails;
	}

	public void setOfficeOutDetails(List<EmpOfficeOutDto> officeOutDetails) {
		this.officeOutDetails = officeOutDetails;
	}
}
