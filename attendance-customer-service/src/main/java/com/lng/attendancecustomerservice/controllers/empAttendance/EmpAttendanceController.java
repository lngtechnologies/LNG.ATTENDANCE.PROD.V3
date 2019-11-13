package com.lng.attendancecustomerservice.controllers.empAttendance;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.empAttendance.EmpAttendanceService;
import com.lng.dto.empAttendance.EmpAttendanceDto;
import com.lng.dto.empAttendance.EmpAttendanceParamDto;
import com.lng.dto.empAttendance.EmpAttendanceResponse;
import com.lng.dto.masters.contractor.ContractorDto;
import com.lng.dto.masters.contractor.ContractorResponse;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/employee/attendance")
public class EmpAttendanceController {
	
	@Autowired
	EmpAttendanceService  empAttendanceService;
	
	@PostMapping(value = "/getAbsentAttendanceByDeptIdAndDate")
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
	
	

}
