package com.lng.attendancecustomerservice.service.empManualAttendance;

import com.lng.dto.employeeAttendance.EmpSummaryDto;
import com.lng.dto.employeeAttendance.EmpSummaryResponse;

public interface EmpSummaryService {
	
	EmpSummaryResponse getEmployeeDetails(EmpSummaryDto empSummaryDto);

}
