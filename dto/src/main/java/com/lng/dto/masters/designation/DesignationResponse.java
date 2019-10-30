package com.lng.dto.masters.designation;

import java.util.List;

import status.Status;

public class DesignationResponse {

	public Status status;
	public List<DesignationDto> data1;
	public DesignationDto data;
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public List<DesignationDto> getData1() {
		return data1;
	}
	public void setData1(List<DesignationDto> data1) {
		this.data1 = data1;
	}
	public DesignationDto getData() {
		return data;
	}
	public void setData(DesignationDto data) {
		this.data = data;
	}


}