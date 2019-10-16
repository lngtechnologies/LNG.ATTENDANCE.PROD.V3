package com.lng.attendancecustomerservice.controllers.empAppSetup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.empAppSetup.EmployeeService;
import com.lng.dto.employee.EmpAppStatusResponseDto;
import com.lng.dto.employee.EmployeeSetup2Dto;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping(value="/employee/setuptwo")
public class EmployeeSetup2Controller {

	@Autowired
	EmployeeService employeeService;
	
	
	@PostMapping(value = "/saveEmployeeSetup2")
    public ResponseEntity<EmpAppStatusResponseDto> findByCustomerId(@RequestBody EmployeeSetup2Dto employeeSetup2Dto) {
		EmpAppStatusResponseDto empAppStatusResponseDto = employeeService.updateEmpAppStatusStageTwo(employeeSetup2Dto);
        if (empAppStatusResponseDto !=null){
            return new ResponseEntity<EmpAppStatusResponseDto>(empAppStatusResponseDto, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
