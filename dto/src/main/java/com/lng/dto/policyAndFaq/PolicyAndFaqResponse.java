package com.lng.dto.policyAndFaq;

import java.util.List;

import status.Status;

public class PolicyAndFaqResponse {
	
	public Status  status;
	
	public  List<PolicyAndFaqDto> data;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<PolicyAndFaqDto> getData() {
		return data;
	}

	public void setData(List<PolicyAndFaqDto> data) {
		this.data = data;
	}
	
	

}
