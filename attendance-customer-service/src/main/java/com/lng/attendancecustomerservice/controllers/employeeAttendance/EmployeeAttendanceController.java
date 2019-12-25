package com.lng.attendancecustomerservice.controllers.employeeAttendance;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.employeeAttendance.EmployeeAttendanceService;
import com.lng.dto.employeeAttendance.CurrentDateDto;
import com.lng.dto.employeeAttendance.EmpSignOutDto;
import com.lng.dto.employeeAttendance.EmpSignOutResponse;
import com.lng.dto.employeeAttendance.EmployeeAttendanceDto;
import com.lng.dto.employeeAttendance.ShiftDetailsDto;
import com.lng.dto.employeeAttendance.ShiftResponseDto;

import status.Status;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping(value="/employee/mark/attendance")
public class EmployeeAttendanceController {

	@Autowired
	EmployeeAttendanceService employeeAttendanceService;

	/* @PostMapping(value = "/save")
	public ResponseEntity<Status> save(@RequestBody List<EmployeeAttendanceDto> employeeAttendanceDto) {
		Status status = employeeAttendanceService.save(employeeAttendanceDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}*/

	@GetMapping(value = "/getCurrentDate")
	public ResponseEntity<CurrentDateDto> getCurrentDate() {
		CurrentDateDto currentDateDto = employeeAttendanceService.getCurrentDate();
		return new ResponseEntity<CurrentDateDto>(currentDateDto, HttpStatus.OK);
	}
	
	@PostMapping(value = "/getShiftDetails")
	public ResponseEntity<ShiftResponseDto> getShiftDetails(@RequestBody ShiftDetailsDto shiftDetailsDto) {
		ShiftResponseDto status = employeeAttendanceService.getShiftDetailsByEmpId(shiftDetailsDto.getEmpId());
		if (status !=null){
			return new ResponseEntity<ShiftResponseDto>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/getOfficeSignOutDetailsByEmpId")
	public ResponseEntity<EmpSignOutResponse> getOfficeSignOutDetailsByEmpId(@RequestBody EmpSignOutDto empSignOutDto) {
		EmpSignOutResponse status = employeeAttendanceService.getOfficeSignOutDetailsByEmpId(empSignOutDto.getEmpId());
		if (status !=null){
			return new ResponseEntity<EmpSignOutResponse>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	 @PostMapping(value = "/signIn")
		public ResponseEntity<Status> saveSignIn(@RequestBody List<EmployeeAttendanceDto> employeeAttendanceDto) {
			Status status = employeeAttendanceService.saveSignIn(employeeAttendanceDto);
			if (status !=null){
				return new ResponseEntity<Status>(status, HttpStatus.CREATED);
			}
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
	 
	 @PostMapping(value = "/signOut")
		public ResponseEntity<Status> saveSignOut(@RequestBody List<EmployeeAttendanceDto> employeeAttendanceDto) {
			Status status = employeeAttendanceService.saveSignOut(employeeAttendanceDto);
			if (status !=null){
				return new ResponseEntity<Status>(status, HttpStatus.CREATED);
			}
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
}
