package com.lng.attendancecustomerservice.service.reports;

import com.lng.dto.reports.EmpTodaySummaryResponse;

public interface EmployeeSummaryService {

	EmpTodaySummaryResponse getSummary(int custId, int empId, int loginId);
}
