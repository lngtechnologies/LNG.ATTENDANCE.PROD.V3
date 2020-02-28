package com.lng.dto.employeeAppSetup;

import java.util.List;

import status.Status;

public class EarlyLeaversResponse {
	
	private  List<ShiftTypeDto> earlyLeaversShiftDetails;

	private  List<EarlyLeaversDto>  earlyLeaversEmpShiftDetails;

	public Status status;

	public List<ShiftTypeDto> getEarlyLeaversShiftDetails() {
		return earlyLeaversShiftDetails;
	}

	public void setEarlyLeaversShiftDetails(List<ShiftTypeDto> earlyLeaversShiftDetails) {
		this.earlyLeaversShiftDetails = earlyLeaversShiftDetails;
	}

	public List<EarlyLeaversDto> getEarlyLeaversEmpShiftDetails() {
		return earlyLeaversEmpShiftDetails;
	}

	public void setEarlyLeaversEmpShiftDetails(List<EarlyLeaversDto> earlyLeaversEmpShiftDetails) {
		this.earlyLeaversEmpShiftDetails = earlyLeaversEmpShiftDetails;
	}


}
