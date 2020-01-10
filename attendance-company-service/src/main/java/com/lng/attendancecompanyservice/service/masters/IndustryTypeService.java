package com.lng.attendancecompanyservice.service.masters;

import com.lng.dto.masters.industryType.IndustryTypeDto;
import com.lng.dto.masters.industryType.IndustryTypeListResponse;
import com.lng.dto.masters.industryType.IndustryTypeResponse;

import status.StatusDto;

public interface IndustryTypeService {
	
	StatusDto saveIndustryType(IndustryTypeDto industryTypeDto);
	
	IndustryTypeListResponse findAllIndustryTypeByIndustryIsActive();
	
	StatusDto updateIndustryType(IndustryTypeDto industryTypeDto);
	
	IndustryTypeResponse findIndustryByIndustryid(Integer industryId);
	
	StatusDto deleteIndustryByIndustryId(Integer industryId);
	
	IndustryTypeListResponse findAllIndustryType();
}
