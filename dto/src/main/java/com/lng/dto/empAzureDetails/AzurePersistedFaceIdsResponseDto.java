package com.lng.dto.empAzureDetails;

import java.util.List;

import status.Status;

public class AzurePersistedFaceIdsResponseDto {

	private List<AzurePersistedFaceIdsDto> facelist;
	
	public Status status;

	public List<AzurePersistedFaceIdsDto> getFacelist() {
		return facelist;
	}

	public void setFacelist(List<AzurePersistedFaceIdsDto> facelist) {
		this.facelist = facelist;
	}	
}
