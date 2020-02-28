package com.lng.dto.employeeAppSetup;

import java.util.List;

import status.Status;

public class LateComersResponse {


	private  List<ShiftTypeDto> lateComersShiftDetails;

	private  List<LateComersDto>  lateComersEmpShiftDetails;

	public Status status;

	public List<ShiftTypeDto> getLateComersShiftDetails() {
		return lateComersShiftDetails;
	}

	public void setLateComersShiftDetails(List<ShiftTypeDto> lateComersShiftDetails) {
		this.lateComersShiftDetails = lateComersShiftDetails;
	}

	public List<LateComersDto> getLateComersEmpShiftDetails() {
		return lateComersEmpShiftDetails;
	}

	public void setLateComersEmpShiftDetails(List<LateComersDto> lateComersEmpShiftDetails) {
		this.lateComersEmpShiftDetails = lateComersEmpShiftDetails;
	}

}
