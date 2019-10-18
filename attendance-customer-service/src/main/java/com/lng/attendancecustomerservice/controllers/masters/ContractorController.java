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

import com.lng.attendancecustomerservice.service.masters.ContractorService;
import com.lng.dto.masters.contractor.ContractorDto;
import com.lng.dto.masters.contractor.ContractorResponse;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master/contractor")
public class ContractorController {
	@Autowired
	ContractorService contractorService;

	@PostMapping(value = "/create")
	public ResponseEntity<ContractorResponse> save(@RequestBody ContractorDto contractorDto) {
		ContractorResponse ContractorDto1 = contractorService.saveContractor(contractorDto);
		if (contractorDto !=null){
			return new ResponseEntity<ContractorResponse>(ContractorDto1, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "/getAll")
	public ResponseEntity<ContractorResponse> getAll() {
		ContractorResponse contractorDto =  contractorService.getAll();
		if(contractorDto.getData().isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<ContractorResponse>(contractorDto, HttpStatus.OK);
	}

	@PostMapping(value="/updateByContractorId")
	public ResponseEntity<status.Status> update(@RequestBody ContractorDto contractorDto){
		status.Status status = contractorService.updateContractorByContractorId(contractorDto);
		if(contractorDto != null){
			return  new ResponseEntity<status.Status>(status, HttpStatus.OK);
		}
		return new ResponseEntity<status.Status>(HttpStatus.NO_CONTENT);
	}
	@PostMapping(value = "/deleteByContractorId")
	public ResponseEntity<ContractorResponse> delete(@RequestBody ContractorDto contractorDto) {
		ContractorResponse contractorResponse = contractorService.deleteByContractorId(contractorDto.getContractorId());
		if(contractorDto!=null){
			return new ResponseEntity<ContractorResponse>(contractorResponse,HttpStatus.OK);
		} return new ResponseEntity(HttpStatus.NO_CONTENT); 
	}

}
