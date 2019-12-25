package com.lng.attendancecustomerservice.service.empAppSetup;

import com.lng.dto.policyAndFaq.PolicyAndFaqDto;
import com.lng.dto.policyAndFaq.PolicyAndFaqResponse;

import status.Status;

public interface PolicyAndFaqService {
	
	//PolicyAndFaqResponse   save(PolicyAndFaqDto policyAndFaqDto);
	
	
	PolicyAndFaqResponse  getPolicyAndFaqByKey(String key);
	
	//PolicyAndFaqResponse  getAll();
	
	//Status  update(PolicyAndFaqDto policyAndFaqDto);
	
	//PolicyAndFaqResponse   update(PolicyAndFaqDto policyAndFaqDto);
	

}
