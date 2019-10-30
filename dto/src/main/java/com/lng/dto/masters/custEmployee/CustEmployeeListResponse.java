package com.lng.dto.masters.custEmployee;

import java.util.List;

import status.Status;

public class CustEmployeeListResponse {

	private List<CustEmployeeDtoTwo> employyeList;
	
	public Status status;

	public List<CustEmployeeDtoTwo> getEmployyeList() {
		return employyeList;
	}

	public void setEmployyeList(List<CustEmployeeDtoTwo> employyeList) {
		this.employyeList = employyeList;
	}
	
	
}
