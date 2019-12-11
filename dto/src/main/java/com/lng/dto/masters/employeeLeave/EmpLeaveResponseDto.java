package com.lng.dto.masters.employeeLeave;

import status.Status;

public class EmpLeaveResponseDto {

	private EmpAppLeaveDto data;
	
	public Status status;

	public EmpAppLeaveDto getData() {
		return data;
	}

	public void setData(EmpAppLeaveDto data) {
		this.data = data;
	}
}
