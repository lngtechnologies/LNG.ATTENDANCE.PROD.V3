package com.lng.dto.masters.designation;

import java.util.List;

import status.Status;

public class DesignationResponse {
	
	public Status status;
	public List<DesignationDto> data;
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public List<DesignationDto> getData() {
		return data;
	}
	public void setData(List<DesignationDto> data) {
		this.data = data;
	}	

}
