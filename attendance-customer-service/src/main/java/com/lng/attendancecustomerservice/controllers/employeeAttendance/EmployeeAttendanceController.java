package com.lng.attendancecustomerservice.controllers.employeeAttendance;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.employeeAttendance.EmployeeAttendanceService;
import com.lng.dto.employeeAttendance.EmployeeAttendanceDto;

import status.Status;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping(value="/employee/mark/attendance")
public class EmployeeAttendanceController {

	@Autowired
	EmployeeAttendanceService employeeAttendanceService;
	
	@PostMapping(value = "/save")
	public ResponseEntity<Status> save(@RequestBody List<EmployeeAttendanceDto> employeeAttendanceDto) {
		Status status = employeeAttendanceService.save(employeeAttendanceDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
}
