package com.lng.attendancecustomerservice.controllers.empMovement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.empMovement.EmpMovementService;
import com.lng.dto.empMovement.EmpMovementDto;
import com.lng.dto.empMovement.EmpMovementParamResponse;
import com.lng.dto.empMovement.EmpMovementResponse;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/employee/movement")
public class EmpMovementController {
	
	@Autowired
	EmpMovementService empMovementService;
	
	@PostMapping(value = "/create")
	public ResponseEntity<EmpMovementResponse> save(@RequestBody EmpMovementDto empMovementDto) {
		EmpMovementResponse empMovementResponse = empMovementService.save(empMovementDto);
		if(empMovementDto !=null){
			return new ResponseEntity<EmpMovementResponse>(empMovementResponse, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/getAll")
	public ResponseEntity<EmpMovementResponse> GetAll(@RequestBody EmpMovementDto empMovementDto) {
		EmpMovementResponse empMovementResponse = empMovementService.getAll(empMovementDto.getRefEmpId(), empMovementDto.getEmpMovementDate());
		if(empMovementDto !=null){
			return new ResponseEntity<EmpMovementResponse>(empMovementResponse, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/getEmpPlaceOfVisitListByEmpId")
	public ResponseEntity<EmpMovementParamResponse> getplaceOfVisit(@RequestBody EmpMovementDto empMovementDto){
		EmpMovementParamResponse empMovementParamResponse = empMovementService.getAllEmpVisitList(empMovementDto.getRefEmpId());
		if(empMovementParamResponse != null) {
			return new ResponseEntity<EmpMovementParamResponse>(empMovementParamResponse,HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}
