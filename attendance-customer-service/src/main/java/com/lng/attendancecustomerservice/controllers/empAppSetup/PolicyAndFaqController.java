package com.lng.attendancecustomerservice.controllers.empAppSetup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.empAppSetup.PolicyAndFaqService;
import com.lng.dto.policyAndFaq.PolicyAndFaqDto;
import com.lng.dto.policyAndFaq.PolicyAndFaqResponse;

@CrossOrigin(origins = "*", maxAge=3600)
@RestController
@RequestMapping(value="/mobile/app/policyandfaq")
public class PolicyAndFaqController {
	
	@Autowired
	PolicyAndFaqService policyAndFaqService;

	
	@PostMapping(value = "/getAllBykey")
	public ResponseEntity<PolicyAndFaqResponse> getAll(@RequestBody PolicyAndFaqDto policyAndFaqDto){
		PolicyAndFaqResponse policyAndFaqResponse =  policyAndFaqService.getPolicyAndFaqByKey(policyAndFaqDto.getKey());
		if(policyAndFaqResponse.getData().isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<PolicyAndFaqResponse>(policyAndFaqResponse, HttpStatus.OK);
	}
}
