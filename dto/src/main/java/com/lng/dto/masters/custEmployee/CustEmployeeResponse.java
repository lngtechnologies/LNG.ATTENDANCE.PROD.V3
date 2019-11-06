package com.lng.dto.masters.custEmployee;

import status.Status;

public class CustEmployeeResponse {

	private CustEmployeeDtoTwo data;
	
	public Status status;

	public CustEmployeeDtoTwo getData() {
		return data;
	}

	public void setData(CustEmployeeDtoTwo data) {
		this.data = data;
	}
	
	
}
