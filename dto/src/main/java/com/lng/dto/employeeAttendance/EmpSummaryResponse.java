package com.lng.dto.employeeAttendance;

import status.Status;

public class EmpSummaryResponse {

	//private EmpSummaryDto empSummaryDetails;
	
	private   EmpAttendanceSumaryDto  summaryDetails;

	public Status status;

	public EmpAttendanceSumaryDto getSummaryDetails() {
		return summaryDetails;
	}

	public void setSummaryDetails(EmpAttendanceSumaryDto summaryDetails) {
		this.summaryDetails = summaryDetails;
	}


}
