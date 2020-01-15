package com.lng.attendancecustomerservice.controllers.empManualAttendance;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.empManualAttendance.EmpManualAttendanceService;
import com.lng.dto.empAttendance.EmpAttendResponseDto;
import com.lng.dto.empAttendance.EmpAttendanceDto;
import com.lng.dto.empAttendance.EmpAttendanceInDto;
import com.lng.dto.empAttendance.EmpAttendanceParamDto;
import com.lng.dto.empAttendance.EmpAttendanceParamDto2;
import com.lng.dto.empAttendance.EmpAttendanceResponse;
import com.lng.dto.employeeAttendance.EmployeeAttendanceDto;

import status.Status;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/employee/manual/attendance")
public class EmpManualAttendanceController {
	
	@Autowired
	EmpManualAttendanceService  empAttendanceService;
	
	@PostMapping(value = "/getEmpAttendanceByDeptIdAndDate")
	public ResponseEntity<EmpAttendanceResponse> edit(@RequestBody EmpAttendanceParamDto empAttendanceDto) {
		EmpAttendanceResponse empAttendanceResponse = empAttendanceService.getEmpAttendanceByDepartment_deptIdAndEmpAttendanceDatetime(empAttendanceDto.getDeptId(), empAttendanceDto.getEmpAttendanceDate());
		if(empAttendanceResponse !=null){
			return new ResponseEntity<EmpAttendanceResponse>(empAttendanceResponse, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	 @PostMapping(value = "/mark")
		public ResponseEntity<Status> saveSignIn(@RequestBody List<EmployeeAttendanceDto> employeeAttendanceDto) {
			Status status = empAttendanceService.saveEmpAttnd(employeeAttendanceDto);
			if (status !=null){
				return new ResponseEntity<Status>(status, HttpStatus.CREATED);
			}
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
	 
	 @PostMapping(value = "/mark/signOut")
		public ResponseEntity<Status> saveSignOut(@RequestBody List<EmployeeAttendanceDto> employeeAttendanceDto) {
			Status status = empAttendanceService.saveSignOut(employeeAttendanceDto);
			if (status !=null){
				return new ResponseEntity<Status>(status, HttpStatus.CREATED);
			}
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
	
	 @PostMapping(value = "/getEmployeeByNameAndDateAndCustomer")
		public ResponseEntity<EmpAttendanceResponse> edit1(@RequestBody EmpAttendanceParamDto2 empAttendanceDto) {
			EmpAttendanceResponse empAttendanceResponse = empAttendanceService.searchEmployeeByNameAndRefCustIdAndEmpAttendanceDatetime(empAttendanceDto.getEmp(), empAttendanceDto.getRefCustId(), empAttendanceDto.getEmpAttendanceDate());
			if(empAttendanceResponse !=null){
				return new ResponseEntity<EmpAttendanceResponse>(empAttendanceResponse, HttpStatus.CREATED);
			}
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
	 
	 @PostMapping(value = "/update/override")
		public ResponseEntity<Status> saveSignIn(@RequestBody EmployeeAttendanceDto employeeAttendanceDto) {
			Status status = empAttendanceService.updateEmpOverRideAttendance(employeeAttendanceDto);
			if (status !=null){
				return new ResponseEntity<Status>(status, HttpStatus.CREATED);
			}
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
	 
	/*@PostMapping(value = "/getAbsentAttendanceByDeptIdAndDate")
	public ResponseEntity<EmpAttendanceResponse> edit(@RequestBody EmpAttendanceParamDto empAttendanceDto) {
		EmpAttendanceResponse empAttendanceResponse = empAttendanceService.getEmpAttendanceByDepartment_deptIdAndEmpAttendanceDatetime(empAttendanceDto.getDeptId(), empAttendanceDto.getEmpAttendanceDatetime());
		if(empAttendanceResponse !=null){
			return new ResponseEntity<EmpAttendanceResponse>(empAttendanceResponse, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/create")
	public ResponseEntity<EmpAttendanceResponse> save(@RequestBody List<EmpAttendanceDto> empAttendanceDto) {
		EmpAttendanceResponse empAttendanceResponse = empAttendanceService.saveEmpAttendance(empAttendanceDto);
		if (empAttendanceDto !=null){
			return new ResponseEntity<EmpAttendanceResponse>(empAttendanceResponse, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	@PostMapping(value = "/getEmployeeByNameAndDateAndCustomer")
	public ResponseEntity<EmpAttendanceResponse> edit1(@RequestBody EmpAttendanceParamDto2 empAttendanceDto) {
		EmpAttendanceResponse empAttendanceResponse = empAttendanceService.searchEmployeeByNameAndRefCustIdAndEmpAttendanceDatetime(empAttendanceDto.getEmp(), empAttendanceDto.getRefCustId(), empAttendanceDto.getEmpAttendanceDatetime());
		if(empAttendanceResponse !=null){
			return new ResponseEntity<EmpAttendanceResponse>(empAttendanceResponse, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	
	
	@PostMapping(value = "/UpdateEmpOverrideAttendanceByCustIdAndEmpIdAndDateAndTime")
	public ResponseEntity<EmpAttendanceResponse> getEmpOverride(@RequestBody EmpAttendanceParamDto2 empAttendanceParamDto2) {
		EmpAttendanceResponse empAttendanceResponse = empAttendanceService.updateEmpOverRideAttendance(empAttendanceParamDto2);
		if(empAttendanceResponse !=null){
			return new ResponseEntity<EmpAttendanceResponse>(empAttendanceResponse, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}*/
	 
	 @PostMapping(value = "/getEmpAttndByDeptIdAndDate")
		public ResponseEntity<EmpAttendResponseDto> edit(@RequestBody EmpAttendanceInDto empAttendanceDto) {
		 EmpAttendResponseDto empAttendanceResponse = empAttendanceService.getEmpAttendanceBydeptIdAndEmpAttendanceDate(empAttendanceDto.getDeptId(), empAttendanceDto.getEmpAttendanceDate());
			if(empAttendanceResponse !=null){
				return new ResponseEntity<EmpAttendResponseDto>(empAttendanceResponse, HttpStatus.CREATED);
			}
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}

	 @PostMapping(value = "/getEmployeeByNameAndDateAndCustomerAndLoginId")
		public ResponseEntity<EmpAttendanceResponse> findEmployee(@RequestBody EmpAttendanceParamDto2 empAttendanceDto) {
			EmpAttendanceResponse empAttendanceResponse = empAttendanceService.searchEmployeeByNameAndRefCustIdAndEmpAttendanceDatetimeAndLoginId(empAttendanceDto.getEmp(), empAttendanceDto.getRefCustId(), empAttendanceDto.getEmpAttendanceDate(),empAttendanceDto.getLoginId());
			if(empAttendanceResponse !=null){
				return new ResponseEntity<EmpAttendanceResponse>(empAttendanceResponse, HttpStatus.CREATED);
			}
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
}
