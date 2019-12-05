package com.lng.attendancecustomerservice.service.reports;

import com.lng.attendancecustomerservice.entity.reports.ReportResponseDto;
import com.lng.attendancecustomerservice.entity.reports.ResponseSummaryReport;
import com.lng.dto.reports.ReportParam;

public interface IReport {
	ReportResponseDto GetAttendanceReport(ReportParam reportParam);
	ResponseSummaryReport GetEmployeeSummaryReport(ReportParam reportParam);
}
