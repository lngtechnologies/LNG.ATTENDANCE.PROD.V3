package com.lng.attendancecustomerservice.controllers.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.reports.EmployeeSummaryService;
import com.lng.dto.employeeAttendance.EmpSummaryDto;
import com.lng.dto.reports.EmpTodaySummaryResponse;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value="/employee/todays/summary")
public class EmployeeSummaryController {

	@Autowired
	EmployeeSummaryService employeeSummaryService;
	
	@PostMapping(value = "/get")
    public ResponseEntity<EmpTodaySummaryResponse> GetAttendanceReport(@RequestBody EmpSummaryDto empSummaryDto) {
		EmpTodaySummaryResponse responseDto = employeeSummaryService.getSummary(empSummaryDto.getCustId(), empSummaryDto.getEmpId(), empSummaryDto.getLoginId());
        if (responseDto !=null){
            return new ResponseEntity<EmpTodaySummaryResponse>(responseDto, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
