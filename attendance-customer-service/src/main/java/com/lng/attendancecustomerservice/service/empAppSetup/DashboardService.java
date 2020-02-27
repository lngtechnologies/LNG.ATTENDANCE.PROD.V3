package com.lng.attendancecustomerservice.service.empAppSetup;

import com.lng.dto.employeeAppSetup.DashboardDto;
import com.lng.dto.reports.TodaysLateComersAndEarlyLeaversResponse;

public interface DashboardService {

	DashboardDto getEmployeeDetails(Integer custId, Integer empId);
	
	TodaysLateComersAndEarlyLeaversResponse getLateComersAndEarlyLeavers(int custId, int empId);
}
