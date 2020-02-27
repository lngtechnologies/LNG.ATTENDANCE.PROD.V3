package com.lng.attendancecustomerservice.controllers.empAppSetup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.empAppSetup.DashboardService;
import com.lng.dto.employeeAppSetup.DashboardDto;
import com.lng.dto.employeeAppSetup.EmployeeDto;
import com.lng.dto.employeeAttendance.EmpSummaryDto;
import com.lng.dto.reports.TodaysLateComersAndEarlyLeaversResponse;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/mobile/app/dashboard")
public class DashboardController {

	@Autowired
	DashboardService dashboardService;
	
	@PostMapping(value = "/getEmployeeSummary")
	public ResponseEntity<DashboardDto> findByEmpId(@RequestBody EmployeeDto employeeDto) {
		DashboardDto dashboardDto = dashboardService.getEmployeeDetails(employeeDto.getRefCustId(), employeeDto.getEmpId());
		return new ResponseEntity<DashboardDto>(dashboardDto, HttpStatus.OK);
	}
	
	@PostMapping(value = "/get/lateComersAndEarlyLeavers")
    public ResponseEntity<TodaysLateComersAndEarlyLeaversResponse> GetLateComersAndEarlyLeavers(@RequestBody EmpSummaryDto empSummaryDto) {
		TodaysLateComersAndEarlyLeaversResponse responseDto = dashboardService.getLateComersAndEarlyLeavers(empSummaryDto.getCustId(), empSummaryDto.getEmpId());
        if (responseDto !=null){
            return new ResponseEntity<TodaysLateComersAndEarlyLeaversResponse>(responseDto, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
