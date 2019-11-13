package com.lng.attendancecustomerservice.controllers.empAppSetup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.empAppSetup.WelcomeScreenService;
import com.lng.dto.employee.EmployeeDto;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapListResponse;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/employee/welcome/screen")
public class WelcomeScerrnController {

	@Autowired
	WelcomeScreenService welcomeScreenService;
	
	@PostMapping(value = "/getBeaconsByEmpId")
	public ResponseEntity<BlockBeaconMapListResponse> getBeaconsByEmpId(@RequestBody EmployeeDto employeeDto) {
		BlockBeaconMapListResponse blockBeaconMapListResponse = welcomeScreenService.getBeaconsByEmpId(employeeDto.getEmpId());
		return new ResponseEntity<BlockBeaconMapListResponse>(blockBeaconMapListResponse, HttpStatus.OK);
	}
}
