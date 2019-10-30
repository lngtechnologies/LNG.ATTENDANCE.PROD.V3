package com.lng.dto.masters.department;

import java.util.List;

import status.Status;

public class DepartmentResponse {

	public Status status;
	public List<DepartmentDto> data1;
	public DepartmentDto data;
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public List<DepartmentDto> getData1() {
		return data1;
	}
	public void setData1(List<DepartmentDto> data1) {
		this.data1 = data1;
	}
	public DepartmentDto getData() {
		return data;
	}
	public void setData(DepartmentDto data) {
		this.data = data;
	}





}