package com.lng.attendancecustomerservice.service.reports;

import java.util.Date;

import com.lng.attendancecustomerservice.entity.reports.EmpEarlyLeaversAndLateComersResponse;
import com.lng.attendancecustomerservice.entity.reports.EmpLeaveReportResponse;
import com.lng.attendancecustomerservice.entity.reports.EmpOfficeOutResponse;
import com.lng.attendancecustomerservice.entity.reports.EmpReportByReportTypeResponse;
import com.lng.attendancecustomerservice.entity.reports.SummaryDetailsResponse;
import com.lng.dto.reports.EmpTodaySummaryResponse;
import com.lng.dto.reports.EmpTodaysLeaveSummaryResponse;
import com.lng.dto.reports.TodaysLateComersAndEarlyLeaversResponse;

public interface EmployeeSummaryService {

	EmpTodaySummaryResponse getSummary(int custId, int empId, int loginId);
	
	EmpTodaysLeaveSummaryResponse getLeaveSummary(int custId, int empId, int loginId);
	
	TodaysLateComersAndEarlyLeaversResponse getLateComersAndEarlyLeavers(int custId, int empId, int loginId);
	
	EmpReportByReportTypeResponse getReportByReportType(int custId, int brId, int deptId, String reportType);
	
	EmpEarlyLeaversAndLateComersResponse getEarlyLeaversAndLateComers(int brId, int deptId, String reportType, Date fromDate, Date todate);
	
	EmpLeaveReportResponse getEmpLeaveReport(int brId, int deptId, String reportType, Date fromDate, Date todate);
	
	EmpOfficeOutResponse getOfficeOutReport(int brId, int deptId, String reportType, Date fromDate, Date todate);
	
	SummaryDetailsResponse getEmployeeSumarryDetails(Integer custId,Integer empId,Integer loginId);
}
