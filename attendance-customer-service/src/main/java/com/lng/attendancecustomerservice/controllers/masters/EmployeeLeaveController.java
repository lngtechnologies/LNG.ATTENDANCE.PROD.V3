package com.lng.attendancecustomerservice.controllers.masters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.masters.EmpLeaveApproveOrCancelService;
import com.lng.attendancecustomerservice.service.masters.EmployeeLeaveService;
import com.lng.dto.masters.empLeaveApproveOrCancel.EmpLeaveDto;
import com.lng.dto.masters.empLeaveApproveOrCancel.EmpLeaveResponseDto;
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
	
	@Autowired
	EmpLeaveApproveOrCancelService EmpLeaveApproveOrCancelService;
	
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
	
	/*@PostMapping(value = "/getEmpLeaveByLoginIdAndCustId")
	public ResponseEntity<EmpLeaveResponseDto> getEmpLeaveByLoginIdAndCustId(@RequestBody EmpLeaveDto empLeaveDto) {
		EmpLeaveResponseDto empLeaveResponseDto = EmpLeaveApproveOrCancelService.getByLoginIdAndCustID(empLeaveDto.getLoginId(), empLeaveDto.getCustId());
		return new ResponseEntity<EmpLeaveResponseDto>(empLeaveResponseDto, HttpStatus.OK);
	}
	
	@PostMapping(value = "/getEmpLeaveAppByLoginIdAndCustId")
	public ResponseEntity<EmpLeaveResponseDto> getEmpLeaveAppByLoginIdAndCustId(@RequestBody EmpLeaveDto empLeaveDto) {
		EmpLeaveResponseDto empLeaveResponseDto = EmpLeaveApproveOrCancelService.getEmpLeaveAppByLoginIdAndCustID(empLeaveDto.getLoginId(), empLeaveDto.getCustId());
		return new ResponseEntity<EmpLeaveResponseDto>(empLeaveResponseDto, HttpStatus.OK);
	}*/
	
	@PostMapping(value = "/getEmpLeaveByLoginIdAndCustId")
	public ResponseEntity<EmpLeaveResponseDto> getEmpLeaveByLoginIdAndCustId(@RequestBody EmpLeaveDto empLeaveDto) {
		EmpLeaveResponseDto empLeaveResponseDto = EmpLeaveApproveOrCancelService.getByLoginIdAndCustIDAndEmpId(empLeaveDto.getLoginId(), empLeaveDto.getCustId());
		return new ResponseEntity<EmpLeaveResponseDto>(empLeaveResponseDto, HttpStatus.OK);
	}
	
	
	@PostMapping(value = "/approve")
	public ResponseEntity<Status> approve(@RequestBody EmployeeLeaveDto employeeLeaveDto) {
		Status status = EmpLeaveApproveOrCancelService.empApproveLeave(employeeLeaveDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/reject")
	public ResponseEntity<Status> reject(@RequestBody EmployeeLeaveDto employeeLeaveDto) {
		Status status = EmpLeaveApproveOrCancelService.empRejectLeave(employeeLeaveDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/approveCancel")
	public ResponseEntity<Status> cancel(@RequestBody EmployeeLeaveDto employeeLeaveDto) {
		Status status = EmpLeaveApproveOrCancelService.empApproveCancelLeave(employeeLeaveDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}
