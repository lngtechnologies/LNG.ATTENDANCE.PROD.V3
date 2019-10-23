package com.lng.attendancecustomerservice.controllers.masters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.masters.CustEmployeeService;
import com.lng.dto.masters.custEmployee.CustEmployeeDto;

import status.StatusDto;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master/employee/")
public class CustEmplyeeController {
	
	@Autowired
	CustEmployeeService custEmployeeService;

	@PostMapping(value = "/create")
    public ResponseEntity<StatusDto> save(@RequestBody CustEmployeeDto custEmployeeDto) {
		StatusDto statusDto = custEmployeeService.save(custEmployeeDto);
        if (custEmployeeDto !=null){
            return new ResponseEntity<StatusDto>(statusDto, HttpStatus.CREATED);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
