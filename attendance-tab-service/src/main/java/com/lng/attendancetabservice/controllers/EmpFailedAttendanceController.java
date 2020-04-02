package com.lng.attendancetabservice.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancetabservice.service.EmpFailedAttendanceService;
import com.lng.dto.empFailedAttendance.EmpFailedAttendanceDto;

import status.Status;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/employee/attendance")
public class EmpFailedAttendanceController {

	@Autowired
	EmpFailedAttendanceService empFailedAttendanceService;
	
	@PostMapping(value = "/saveEmpFailedAttendance")
	public ResponseEntity<Status> save(@RequestBody List<EmpFailedAttendanceDto> empFailedAttendanceDto) {
		Status status = empFailedAttendanceService.saveEmpFailedAttendance(empFailedAttendanceDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}
