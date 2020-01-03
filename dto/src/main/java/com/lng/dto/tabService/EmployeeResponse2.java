package com.lng.dto.tabService;

import status.Status;

public class EmployeeResponse2 {
	
	public EmployeeDto3 data;

	public Status status;

	
	public EmployeeDto3 getData() {
		return data;
	}

	public void setData(EmployeeDto3 data) {
		this.data = data;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	

}
