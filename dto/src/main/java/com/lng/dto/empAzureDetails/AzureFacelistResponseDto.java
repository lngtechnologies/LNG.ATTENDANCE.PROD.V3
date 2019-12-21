package com.lng.dto.empAzureDetails;

import java.util.List;

import status.Status;

public class AzureFacelistResponseDto {

	private List<AzureFacelistDto> facelist;
	
	public Status status;

	public List<AzureFacelistDto> getFacelist() {
		return facelist;
	}

	public void setFacelist(List<AzureFacelistDto> facelist) {
		this.facelist = facelist;
	}	
}
