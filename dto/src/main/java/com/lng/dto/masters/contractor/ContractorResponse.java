package com.lng.dto.masters.contractor;

import java.util.List;

import status.Status;

public class ContractorResponse {
	  public Status status; 
	  public List<ContractorDto> data;
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public List<ContractorDto> getData() {
		return data;
	}
	public void setData(List<ContractorDto> data) {
		this.data = data;
	}
	  
	  

}
