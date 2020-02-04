package com.lng.attendancetabservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancetabservice.service.EmpAttendanceService;
import com.lng.dto.tabService.EmpAttendanceDto1;

import status.Status;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/employee/attendance")
public class EmpAttendanceController {

	@Autowired
	EmpAttendanceService  empAttendanceService;

	@PostMapping(value = "/markIn")
	public ResponseEntity<Status> saveIn(@RequestBody EmpAttendanceDto1 empAttendanceDto1) {
		Status status = empAttendanceService.saveEmpAttndIn(empAttendanceDto1);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}


	@PostMapping(value = "/markOut")
	public ResponseEntity<Status> saveOut(@RequestBody EmpAttendanceDto1 empAttendanceDto1) {
		Status status = empAttendanceService.saveEmpAttndOut(empAttendanceDto1);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	/*@PostMapping(value = "/mark")
	public ResponseEntity<Status> save(@RequestBody EmpAttendanceDto1 empAttendanceDto1) {
		Status status = empAttendanceService.saveEmployeeAttnd(empAttendanceDto1);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}*/
}
