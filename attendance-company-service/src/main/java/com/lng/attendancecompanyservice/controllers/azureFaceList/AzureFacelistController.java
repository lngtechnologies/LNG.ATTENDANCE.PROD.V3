package com.lng.attendancecompanyservice.controllers.azureFaceList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecompanyservice.service.azureEmpDetails.RegisteredEmployeeService;
import com.lng.dto.customer.BranchDto;
import status.Status;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping(value="/azure/fecelist")
public class AzureFacelistController {

	@Autowired
	RegisteredEmployeeService registeredEmployeeService;
	
	/*@PostMapping(value = "/getBranchDetailsByCustomerId")
	public ResponseEntity<Status> edit1(@RequestBody BranchDto branchDto){
		Status status = registeredEmployeeService.getRegisteredEmpDetailsByBranchId(branchDto.getBrId());
		return new ResponseEntity<Status>(status, HttpStatus.OK);
	}*/
}
