package com.lng.attendancetabservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancetabservice.service.EmployeeService;
import com.lng.dto.tabService.EmployeeDto1;
import com.lng.dto.tabService.EmployeeResponse1;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping(value="/employee")
public class EmployeeController {
	@Autowired
	EmployeeService employeeService;
	
	
	@PostMapping(value = "/getByBrCodeAndCustCode") 
	public ResponseEntity<EmployeeResponse1> findByBrId(@RequestBody EmployeeDto1 employeeDto) {
		EmployeeResponse1 employeeResponse1 = employeeService.verifyEmpNameAndMobileNo(employeeDto.getRefBrId(), employeeDto.getRefCustId(), employeeDto.getEmpName(), employeeDto.getEmpMobile());
		if (employeeResponse1 !=null){
			return new ResponseEntity<EmployeeResponse1>(employeeResponse1, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}

}
