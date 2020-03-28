package com.lng.attendancecustomerservice.controllers.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.entity.reports.EmpEarlyLeaversAndLateComersResponse;
import com.lng.attendancecustomerservice.entity.reports.EmpLeaveReportResponse;
import com.lng.attendancecustomerservice.entity.reports.EmpOfficeOutResponse;
import com.lng.attendancecustomerservice.entity.reports.EmpReportByReportTypeResponse;
import com.lng.attendancecustomerservice.entity.reports.SummaryDetailsResponse;
import com.lng.attendancecustomerservice.service.reports.EmployeeSummaryService;
import com.lng.dto.employeeAttendance.EmpSummaryDto;
import com.lng.dto.reports.EmpReportParam;
import com.lng.dto.reports.EmpTodaySummaryResponse;
import com.lng.dto.reports.EmpTodaysLeaveSummaryResponse;
import com.lng.dto.reports.TodaysLateComersAndEarlyLeaversResponse;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value="/employee")
public class EmployeeSummaryController {

	@Autowired
	EmployeeSummaryService employeeSummaryService;
	
	@PostMapping(value = "/get/todays/summary")
    public ResponseEntity<EmpTodaySummaryResponse> GetSummary(@RequestBody EmpSummaryDto empSummaryDto) {
		if(empSummaryDto.getLoginId() == null) {
			empSummaryDto.setLoginId(0);
		}
		if(empSummaryDto.getEmpId() == null) {
			empSummaryDto.setEmpId(0);
		}
		EmpTodaySummaryResponse responseDto = employeeSummaryService.getSummary(empSummaryDto.getCustId(), empSummaryDto.getEmpId(), empSummaryDto.getLoginId());
        if (responseDto !=null){
            return new ResponseEntity<EmpTodaySummaryResponse>(responseDto, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
	
	@PostMapping(value = "/get/todays/leave/summary")
    public ResponseEntity<EmpTodaysLeaveSummaryResponse> GetLeaveSummary(@RequestBody EmpSummaryDto empSummaryDto) {
		if(empSummaryDto.getLoginId() == null) {
			empSummaryDto.setLoginId(0);
		}
		if(empSummaryDto.getEmpId() == null) {
			empSummaryDto.setEmpId(0);
		}
		EmpTodaysLeaveSummaryResponse responseDto = employeeSummaryService.getLeaveSummary(empSummaryDto.getCustId(), empSummaryDto.getEmpId(), empSummaryDto.getLoginId());
        if (responseDto !=null){
            return new ResponseEntity<EmpTodaysLeaveSummaryResponse>(responseDto, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
	
	@PostMapping(value = "/get/lateComersAndEarlyLeavers")
    public ResponseEntity<TodaysLateComersAndEarlyLeaversResponse> GetLateComersAndEarlyLeavers(@RequestBody EmpSummaryDto empSummaryDto) {
		if(empSummaryDto.getLoginId() == null) {
			empSummaryDto.setLoginId(0);
		}
		if(empSummaryDto.getEmpId() == null) {
			empSummaryDto.setEmpId(0); 
		}
		TodaysLateComersAndEarlyLeaversResponse responseDto = employeeSummaryService.getLateComersAndEarlyLeavers(empSummaryDto.getCustId(), empSummaryDto.getEmpId(), empSummaryDto.getLoginId());
        if (responseDto !=null){
            return new ResponseEntity<TodaysLateComersAndEarlyLeaversResponse>(responseDto, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
	
	@PostMapping(value = "/get/empAbsentReportByReportType")
    public ResponseEntity<EmpReportByReportTypeResponse> GetAbsentReport(@RequestBody EmpReportParam empReportParam) {
		EmpReportByReportTypeResponse responseDto = employeeSummaryService.getReportByReportType(empReportParam.getCustId(), empReportParam.getBrId(), empReportParam.getDeptId(), empReportParam.getReportType(), empReportParam.getFromDate());
        if (responseDto !=null){
            return new ResponseEntity<EmpReportByReportTypeResponse>(responseDto, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

	@PostMapping(value = "/get/empEarlyLeaversAndLateComersByReportType")
    public ResponseEntity<EmpEarlyLeaversAndLateComersResponse> GetEarlyLeaversAndLateComers(@RequestBody EmpReportParam empReportParam) {
		EmpEarlyLeaversAndLateComersResponse responseDto = employeeSummaryService.getEarlyLeaversAndLateComers(empReportParam.getBrId(), empReportParam.getDeptId(), empReportParam.getReportType(), empReportParam.getFromDate(), empReportParam.getToDate());
        if (responseDto !=null){
            return new ResponseEntity<EmpEarlyLeaversAndLateComersResponse>(responseDto, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
	
	@PostMapping(value = "/get/empLeaveReportByReportType")
    public ResponseEntity<EmpLeaveReportResponse> GetLeavereport(@RequestBody EmpReportParam empReportParam) {
		EmpLeaveReportResponse responseDto = employeeSummaryService.getEmpLeaveReport(empReportParam.getBrId(), empReportParam.getDeptId(), empReportParam.getReportType(), empReportParam.getFromDate(), empReportParam.getToDate());
        if (responseDto !=null){
            return new ResponseEntity<EmpLeaveReportResponse>(responseDto, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
	
	@PostMapping(value = "/get/empOfficeOutReportByReportType")
    public ResponseEntity<EmpOfficeOutResponse> GetOfficeOutReport(@RequestBody EmpReportParam empReportParam) {
		EmpOfficeOutResponse responseDto = employeeSummaryService.getOfficeOutReport(empReportParam.getBrId(), empReportParam.getDeptId(), empReportParam.getReportType(), empReportParam.getFromDate(), empReportParam.getToDate());
        if (responseDto !=null){
            return new ResponseEntity<EmpOfficeOutResponse>(responseDto, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
	@PostMapping(value = "/get/EmployeeSumarryDetails")
    public ResponseEntity<SummaryDetailsResponse> GetEmployeeSumaryDetails(@RequestBody EmpSummaryDto empSummaryDto) {
		if(empSummaryDto.getLoginId() == null) {
			empSummaryDto.setLoginId(0);
		}
		if(empSummaryDto.getEmpId() == null) {
			empSummaryDto.setEmpId(0); 
		}
		SummaryDetailsResponse summaryDetailsResponse = employeeSummaryService.getEmployeeSumarryDetails(empSummaryDto.getCustId(), empSummaryDto.getEmpId(), empSummaryDto.getLoginId());
        if (summaryDetailsResponse !=null){
            return new ResponseEntity<SummaryDetailsResponse>(summaryDetailsResponse, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
