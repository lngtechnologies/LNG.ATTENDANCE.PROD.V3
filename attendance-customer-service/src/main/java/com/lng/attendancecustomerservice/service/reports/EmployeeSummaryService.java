package com.lng.attendancecustomerservice.service.reports;

import com.lng.dto.reports.EmpTodaySummaryResponse;
import com.lng.dto.reports.EmpTodaysLeaveSummaryResponse;
import com.lng.dto.reports.TodaysLateComersAndEarlyLeaversResponse;

public interface EmployeeSummaryService {

	EmpTodaySummaryResponse getSummary(int custId, int empId, int loginId);
	
	EmpTodaysLeaveSummaryResponse getLeaveSummary(int custId, int empId, int loginId);
	
	TodaysLateComersAndEarlyLeaversResponse getLateComersAndEarlyLeavers(int custId, int empId, int loginId);
}
