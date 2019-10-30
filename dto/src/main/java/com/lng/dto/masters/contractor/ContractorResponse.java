package com.lng.dto.masters.contractor;

import java.util.List;

import status.Status;

public class ContractorResponse {
	public Status status; 
	public List<ContractorDto> data1;

	public ContractorDto data;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<ContractorDto> getData1() {
		return data1;
	}

	public void setData1(List<ContractorDto> data1) {
		this.data1 = data1;
	}

	public ContractorDto getData() {
		return data;
	}

	public void setData(ContractorDto data) {
		this.data = data;
	}




}