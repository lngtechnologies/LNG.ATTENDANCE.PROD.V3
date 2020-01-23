package com.lng.attendancecustomerservice.controllers.empAppSetup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.empAppSetup.EmpLeaveService;
import com.lng.dto.masters.employeeLeave.CustLeaveTrypeListDto;
import com.lng.dto.masters.employeeLeave.EmpCancelLeaveDto;
import com.lng.dto.masters.employeeLeave.EmpLeaveResponseDto;
import com.lng.dto.masters.employeeLeave.EmployeeLeaveDto;

import status.Status;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/mobile/app/employee/leave")
public class EmpLeaveController {

	@Autowired
	EmpLeaveService empLeaveService;
	
	@PostMapping(value = "/getLeaveListByCustId")
	public ResponseEntity<CustLeaveTrypeListDto> findByCustId(@RequestBody CustLeaveTrypeListDto custLeaveTrypeListDto) {
		CustLeaveTrypeListDto custLeaveTrypeListDto1 = empLeaveService.getLeaveListByCustId(custLeaveTrypeListDto.getCustId());
		return new ResponseEntity<CustLeaveTrypeListDto>(custLeaveTrypeListDto1, HttpStatus.OK);
	}
	
	@PostMapping(value = "/apply")
	public ResponseEntity<Status> save(@RequestBody EmployeeLeaveDto employeeLeaveDto) {
		Status status = empLeaveService.saveEmpLeave(employeeLeaveDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/getEmpLeaveByEmpId")
	public ResponseEntity<EmpLeaveResponseDto> findByEmpId(@RequestBody EmployeeLeaveDto empLeaveDto) {
		EmpLeaveResponseDto empLeaveResponseDto = empLeaveService.getEmpLeaveByEmpId(empLeaveDto.getEmpId());
		return new ResponseEntity<EmpLeaveResponseDto>(empLeaveResponseDto, HttpStatus.OK);
	}
	
	@PostMapping(value = "/cancel")
	public ResponseEntity<Status> cancelEmpLeave(@RequestBody EmpCancelLeaveDto empCancelLeaveDto) {
		Status status = empLeaveService.cancelLeave(empCancelLeaveDto.getCustId(), empCancelLeaveDto.getEmpLeaveId());
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}
