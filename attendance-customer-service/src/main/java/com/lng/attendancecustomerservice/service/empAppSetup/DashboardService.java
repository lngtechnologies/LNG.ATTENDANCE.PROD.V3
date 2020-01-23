package com.lng.attendancecustomerservice.service.empAppSetup;

import com.lng.dto.employeeAppSetup.DashboardDto;

public interface DashboardService {

	DashboardDto getEmployeeDetails(Integer custId, Integer empId);
}
