package com.lng.dto.employeeAttendance;

import status.Status;

public class EmpSummaryResponse {

	private EmpSummaryDto empSummaryDetails;

	public Status status;

	public EmpSummaryDto getEmpSummaryDetails() {
		return empSummaryDetails;
	}

	public void setEmpSummaryDetails(EmpSummaryDto empSummaryDetails) {
		this.empSummaryDetails = empSummaryDetails;
	}


}
