package com.lng.attendancecustomerservice.controllers.empManualAttendance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.empManualAttendance.EmpSummaryService;
import com.lng.dto.employeeAttendance.EmpSummaryDto;
import com.lng.dto.employeeAttendance.EmpSummaryResponse;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/employee/summary")
public class EmpSummaryController {
	
	@Autowired
	EmpSummaryService empSummaryService;
	
	
	@PostMapping(value = "/reportsforCurdate")
	public ResponseEntity<EmpSummaryResponse> edit(@RequestBody EmpSummaryDto empSummaryDto) {
		EmpSummaryResponse EmpSummaryResponse1 = empSummaryService.getEmployeeDetails(empSummaryDto);
		if(EmpSummaryResponse1 !=null){
			return new ResponseEntity<EmpSummaryResponse>(EmpSummaryResponse1, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

}
