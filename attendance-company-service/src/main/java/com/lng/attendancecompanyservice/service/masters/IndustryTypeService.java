package com.lng.attendancecompanyservice.service.masters;

import com.lng.dto.IndustryType.IndustryTypeDto;
import com.lng.dto.IndustryType.IndustryTypeListResponse;
import com.lng.dto.IndustryType.IndustryTypeResponse;

import status.StatusDto;

public interface IndustryTypeService {
	
	StatusDto saveIndustryType(IndustryTypeDto industryTypeDto);
	
	IndustryTypeListResponse findAllIndustryType();
	
	StatusDto updateIndustryType(IndustryTypeDto industryTypeDto);
	
	IndustryTypeResponse findIndustryByIndustryid(Integer industryId);
	
	StatusDto deleteIndustryByIndustryId(Integer industryId);
}
