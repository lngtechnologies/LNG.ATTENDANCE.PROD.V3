package com.lng.attendancecompanyservice.controllers.masters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecompanyservice.service.masters.PolicyAndFaqService;
import com.lng.dto.policyAndFaq.PolicyAndFaqDto;
import com.lng.dto.policyAndFaq.PolicyAndFaqResponse;

@CrossOrigin(origins = "*", maxAge=3600)
@RestController
@RequestMapping(value="/master/policyandfaq")
public class PolicyAndFaqController {
	
	@Autowired
	PolicyAndFaqService policyAndFaqService;

	@PostMapping(value = "/create")
	public ResponseEntity<PolicyAndFaqResponse> save(@RequestBody PolicyAndFaqDto policyAndFaqDto) {
		PolicyAndFaqResponse policyAndFaqResponse = policyAndFaqService.save(policyAndFaqDto);
		if (policyAndFaqDto !=null){
			return new ResponseEntity<PolicyAndFaqResponse>(policyAndFaqResponse, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "/getAll")
	public ResponseEntity<PolicyAndFaqResponse> getAll() {
		PolicyAndFaqResponse policyAndFaqResponse =  policyAndFaqService.getAll();
		if(policyAndFaqResponse.getData().isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<PolicyAndFaqResponse>(policyAndFaqResponse, HttpStatus.OK);
	}

}
