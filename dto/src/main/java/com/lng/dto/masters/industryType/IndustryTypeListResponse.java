package com.lng.dto.masters.industryType;

import java.util.List;

import status.Status;

public class IndustryTypeListResponse {

	private	List<IndustryTypeDto> industryTypeDtoList;
	
	public Status status;

	public List<IndustryTypeDto> getIndustryTypeDtoList() {
		return industryTypeDtoList;
	}

	public void setIndustryTypeDtoList(List<IndustryTypeDto> industryTypeDtoList) {
		this.industryTypeDtoList = industryTypeDtoList;
	}
	
	
}
