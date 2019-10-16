package com.lng.attendancecustomerservice.controllers.masters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.masters.EmployeeTypeService;
import com.lng.dto.employeeType.EmployeeTypeDto;
import com.lng.dto.employeeType.EmployeeTypeListResponseDto;
import com.lng.dto.employeeType.StatusDto;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping(value="/type")
public class EmployeeTypeController {

	@Autowired
	EmployeeTypeService employeeTypeService;
	
	@PostMapping(value = "/create")
    public ResponseEntity<StatusDto> save(@RequestBody EmployeeTypeDto employeeTypeDto) {
		StatusDto statusDto = employeeTypeService.save(employeeTypeDto);
        if (employeeTypeDto !=null){
            return new ResponseEntity<StatusDto>(statusDto, HttpStatus.CREATED);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
	
	@GetMapping(value = "/findAllEmployeeType")
	public ResponseEntity<EmployeeTypeListResponseDto> findAll() {
		EmployeeTypeListResponseDto employeeTypeListResponseDto = employeeTypeService.findAll(); 
       if(employeeTypeListResponseDto.getEmployeeTypeDtoList().isEmpty()) {
           return new ResponseEntity(HttpStatus.NO_CONTENT);
       }
       return new ResponseEntity<EmployeeTypeListResponseDto>(employeeTypeListResponseDto, HttpStatus.OK);
   }
	
	@PostMapping(value = "/update")
    public ResponseEntity<StatusDto> update(@RequestBody EmployeeTypeDto employeeTypeDto) {
		StatusDto statusDto = employeeTypeService.updateEmpType(employeeTypeDto);
        if (employeeTypeDto !=null){
            return new ResponseEntity<StatusDto>(statusDto, HttpStatus.CREATED);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
