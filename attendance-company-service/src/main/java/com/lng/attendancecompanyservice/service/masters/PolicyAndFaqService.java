package com.lng.attendancecompanyservice.service.masters;

import com.lng.dto.policyAndFaq.PolicyAndFaqDto;
import com.lng.dto.policyAndFaq.PolicyAndFaqResponse;

public interface PolicyAndFaqService {
	
	PolicyAndFaqResponse   save(PolicyAndFaqDto policyAndFaqDto);
	
	
	PolicyAndFaqResponse  getPolicyAndFaqByKey(String key);
	
	PolicyAndFaqResponse  getAll();
	
	//PolicyAndFaqResponse   update(PolicyAndFaqDto policyAndFaqDto);
	

}
