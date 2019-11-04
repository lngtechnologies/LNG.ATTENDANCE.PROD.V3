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

import com.lng.attendancecustomerservice.service.masters.DesignationService;
import com.lng.dto.masters.designation.DesignationDto;
import com.lng.dto.masters.designation.DesignationResponse;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master/designation")
public class DesignationController {
	@Autowired
	DesignationService designationService;

	@PostMapping(value = "/create")
	public ResponseEntity<DesignationResponse> save(@RequestBody DesignationDto designationDto) {
		DesignationResponse DesignationDto1 = designationService.saveDesignation(designationDto);
		return new ResponseEntity<DesignationResponse>(DesignationDto1, HttpStatus.OK);
	}

	@GetMapping(value = "/getAll")
	public ResponseEntity<DesignationResponse> getAll() {
		DesignationResponse designationDto = designationService.getAll();
		if(designationDto.getData1().isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<DesignationResponse>(designationDto, HttpStatus.OK);
	}

	@PostMapping(value="/updateBydesignationId")
	public ResponseEntity<status.Status> update(@RequestBody DesignationDto designationDto){
		status.Status status = designationService.updateDesignationBydesignationId(designationDto);
		if(designationDto != null){
			return new ResponseEntity<status.Status>(status, HttpStatus.OK);
		}
		return new ResponseEntity<status.Status>(HttpStatus.NO_CONTENT);
	}
	@PostMapping(value = "/deleteBydesignationId")
	public ResponseEntity<DesignationResponse> delete(@RequestBody DesignationDto designationDto) {
		DesignationResponse designationResponse = designationService.deleteByDesignationId(designationDto.getDesignationId());
		if(designationDto!=null){
			return new ResponseEntity<DesignationResponse>(designationResponse,HttpStatus.OK);
		} return new ResponseEntity(HttpStatus.NO_CONTENT); 
	}

	@PostMapping(value = "/getDesignationDetailsByDesignationId")
	public ResponseEntity<DesignationResponse> findByDesignationId(@RequestBody DesignationDto designationDto) {
		DesignationResponse designationResponse = designationService.getDesignationByDesignationId(designationDto.getDesignationId());
		if (designationResponse !=null){
			return new ResponseEntity<DesignationResponse>(designationResponse, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping(value = "/getAllByCustId")
	public ResponseEntity<DesignationResponse> getAllByCustId(@RequestBody DesignationDto designationDto) {
		DesignationResponse designationResponse = designationService.getAllByCustId(designationDto.getRefCustId());
		return new ResponseEntity<DesignationResponse>(designationResponse, HttpStatus.OK);
	}
}

