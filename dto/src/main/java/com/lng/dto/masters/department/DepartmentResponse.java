package com.lng.dto.masters.department;

import java.util.List;

import status.Status;

public class DepartmentResponse {
	
	public Status status;
	public List<DepartmentDto> data;
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public List<DepartmentDto> getData() {
		return data;
	}
	public void setData(List<DepartmentDto> data) {
		this.data = data;
	}	
	
	
	

}
