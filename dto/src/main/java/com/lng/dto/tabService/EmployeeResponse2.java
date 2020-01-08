package com.lng.dto.tabService;

import status.Status;

public class EmployeeResponse2 {
	
	public EmployeeDto3 shiftData;

	public Status status;

	public EmployeeDto3 getShiftData() {
		return shiftData;
	}

	public void setShiftData(EmployeeDto3 shiftData) {
		this.shiftData = shiftData;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	

}
