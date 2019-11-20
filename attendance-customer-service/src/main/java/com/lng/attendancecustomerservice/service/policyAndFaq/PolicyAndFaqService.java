package com.lng.attendancecustomerservice.service.policyAndFaq;

import com.lng.dto.policyAndFaq.PolicyAndFaqDto;
import com.lng.dto.policyAndFaq.PolicyAndFaqResponse;

public interface PolicyAndFaqService {
	
	PolicyAndFaqResponse   save(PolicyAndFaqDto policyAndFaqDto);
	
	
	PolicyAndFaqResponse  getAll();
	
	
	

}
