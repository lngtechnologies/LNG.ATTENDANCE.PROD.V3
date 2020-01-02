package com.lng.dto.empAttendance;

import status.Status;

public class EmpAttendResponseDto {

	public Status status;
	
	private EmpManualAttendance response;

	public EmpManualAttendance getResponse() {
		return response;
	}

	public void setResponse(EmpManualAttendance response) {
		this.response = response;
	}

	
}
