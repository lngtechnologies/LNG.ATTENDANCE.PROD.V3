package com.lng.attendancecustomerservice.controllers.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.entity.reports.ReportResponseDto;
import com.lng.attendancecustomerservice.entity.reports.ResponseSummaryReport;
import com.lng.attendancecustomerservice.service.reports.IReport;
import com.lng.dto.reports.EmployeeDetailsDto;
import com.lng.dto.reports.EmployeeDtailsResponse;
import com.lng.dto.reports.ReportParam;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value="/reports")
public class ReportController {
	
	@Autowired IReport iReport;

	@PostMapping(value = "/get")
    public ResponseEntity<ReportResponseDto> GetAttendanceReport(@RequestBody ReportParam reportParam) {
		ReportResponseDto responseDto = iReport.GetAttendanceReport(reportParam);
        if (responseDto !=null){
            return new ResponseEntity<ReportResponseDto>(responseDto, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
	
	@PostMapping(value = "/summary/employee")
	public ResponseEntity<ResponseSummaryReport> GetSummaryReportByEmployee(@RequestBody ReportParam reportParam) {
		ResponseSummaryReport responseSummaryReport = iReport.GetEmployeeSummaryReport(reportParam);
		if (responseSummaryReport !=null){
            return new ResponseEntity<ResponseSummaryReport>(responseSummaryReport, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
	

	@PostMapping(value = "/getEmployeeListByCustIdAndEmpId")
	public ResponseEntity<EmployeeDtailsResponse> edit(@RequestBody EmployeeDetailsDto employeeDetailsDto) {
		EmployeeDtailsResponse employeeDtailsResponse = iReport.getEmployeeDetails(employeeDetailsDto.getEmpId(),employeeDetailsDto.getCustId());
		if(employeeDtailsResponse !=null){
			return new ResponseEntity<EmployeeDtailsResponse>(employeeDtailsResponse, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
}
