package com.lng.dto.reports;

import status.Status;

public class EmpTodaysLeaveSummaryResponse {

	private EmpTodaysLeaveSummaryDto todaysLeaveSummary;
	
	public Status status;

	public EmpTodaysLeaveSummaryDto getTodaysLeaveSummary() {
		return todaysLeaveSummary;
	}

	public void setTodaysLeaveSummary(EmpTodaysLeaveSummaryDto todaysLeaveSummary) {
		this.todaysLeaveSummary = todaysLeaveSummary;
	}
}
