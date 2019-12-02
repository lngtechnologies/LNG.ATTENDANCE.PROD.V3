package com.lng.dto.employeeAttendance;

import status.Status;

public class EmpSignOutResponse {

	private EmpSignOutDto empSignOutDto;
	
	public Status status;

	public EmpSignOutDto getEmpSignOutDto() {
		return empSignOutDto;
	}

	public void setEmpSignOutDto(EmpSignOutDto empSignOutDto) {
		this.empSignOutDto = empSignOutDto;
	}

}
