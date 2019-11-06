package com.lng.attendancecompanyservice.controllers.masters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecompanyservice.service.masters.EmployeeTypeService;
import com.lng.dto.masters.employeeType.EmployeeTypeListResponseDto;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master/employee/type")
public class EmployeeTypeController {

	@Autowired
	EmployeeTypeService employeeTypeService;
	
	@GetMapping(value = "/findAll")
	public ResponseEntity<EmployeeTypeListResponseDto> findAll() {
		EmployeeTypeListResponseDto employeeTypeListResponseDto = employeeTypeService.findAll(); 
       if(employeeTypeListResponseDto.getEmployeeTypeDtoList().isEmpty()) {
           return new ResponseEntity(HttpStatus.NO_CONTENT);
       }
       return new ResponseEntity<EmployeeTypeListResponseDto>(employeeTypeListResponseDto, HttpStatus.OK);
   }
}
