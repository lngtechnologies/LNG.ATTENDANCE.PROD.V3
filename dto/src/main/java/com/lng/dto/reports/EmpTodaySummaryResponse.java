package com.lng.dto.reports;

import status.Status;

public class EmpTodaySummaryResponse {

	private EmployeeTodaysSummaryDto empSummary;
	
	public Status status;

	public EmployeeTodaysSummaryDto getEmpSummary() {
		return empSummary;
	}

	public void setEmpSummary(EmployeeTodaysSummaryDto empSummary) {
		this.empSummary = empSummary;
	}

}
