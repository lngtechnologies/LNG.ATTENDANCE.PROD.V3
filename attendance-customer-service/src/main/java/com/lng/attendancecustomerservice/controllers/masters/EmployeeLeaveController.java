package com.lng.attendancecustomerservice.controllers.masters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.masters.EmployeeLeaveService;
import com.lng.dto.masters.custUserMgmt.CustUserMgmtDto;
import com.lng.dto.masters.employeeLeave.BranchListDto;
import com.lng.dto.masters.employeeLeave.CustLeaveTrypeListDto;
import com.lng.dto.masters.employeeLeave.EmployeeDtatListDto;
import com.lng.dto.masters.employeeLeave.EmployeeLeaveDto;

import status.Status;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master/employee/leave")
public class EmployeeLeaveController {

	@Autowired
	EmployeeLeaveService employeeLeaveService;
	
	@PostMapping(value = "/getBranchListByCustId")
	public ResponseEntity<BranchListDto> findByCustId(@RequestBody BranchListDto branchListDto) {
		BranchListDto branchListDto1 = employeeLeaveService.getBranchListByCustId(branchListDto.getCustId());
		return new ResponseEntity<BranchListDto>(branchListDto1, HttpStatus.OK);
	}
	
	@PostMapping(value = "/getLeaveListByCustId")
	public ResponseEntity<CustLeaveTrypeListDto> findByCustId(@RequestBody CustLeaveTrypeListDto custLeaveTrypeListDto) {
		CustLeaveTrypeListDto custLeaveTrypeListDto1 = employeeLeaveService.getLeaveListByCustId(custLeaveTrypeListDto.getCustId());
		return new ResponseEntity<CustLeaveTrypeListDto>(custLeaveTrypeListDto1, HttpStatus.OK);
	}
	
	@PostMapping(value = "/getEmpDataByBranchId")
	public ResponseEntity<EmployeeDtatListDto> findByCustId(@RequestBody EmployeeDtatListDto employeeDtatListDto) {
		EmployeeDtatListDto employeeDtatListDto1 = employeeLeaveService.getEmpDataByBrID(employeeDtatListDto.getBrId());
		return new ResponseEntity<EmployeeDtatListDto>(employeeDtatListDto1, HttpStatus.OK);
	}
	
	@PostMapping(value = "/apply")
	public ResponseEntity<Status> save(@RequestBody EmployeeLeaveDto employeeLeaveDto) {
		Status status = employeeLeaveService.saveEmpLeave(employeeLeaveDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}
