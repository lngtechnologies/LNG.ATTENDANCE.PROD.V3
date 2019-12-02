package com.lng.dto.employeeAttendance;

import status.Status;

public class ShiftResponseDto {

	private ShiftDetailsDto detailsDto;
	
	public Status status;

	public ShiftDetailsDto getDetailsDto() {
		return detailsDto;
	}

	public void setDetailsDto(ShiftDetailsDto detailsDto) {
		this.detailsDto = detailsDto;
	}

	
	
}
