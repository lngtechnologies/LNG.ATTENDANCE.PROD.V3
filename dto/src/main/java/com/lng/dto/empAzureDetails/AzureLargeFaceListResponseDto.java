package com.lng.dto.empAzureDetails;

import java.util.List;

import status.Status;

public class AzureLargeFaceListResponseDto {

	private List<AzureLargeFaceListDto> largeFaceList;
	
	public Status status;

	public List<AzureLargeFaceListDto> getLargeFaceList() {
		return largeFaceList;
	}

	public void setLargeFaceList(List<AzureLargeFaceListDto> largeFaceList) {
		this.largeFaceList = largeFaceList;
	}
}
