package com.lng.dto.employeeAppSetup;

import java.util.List;

import status.Status;

public class AbsentDetailsResponse {
	
	private  List<ShiftTypeDto> absentShiftDetails;

	private  List<AbsentDto>  absentEmpShiftDetails;

	public Status status;

	public List<ShiftTypeDto> getAbsentShiftDetails() {
		return absentShiftDetails;
	}

	public void setAbsentShiftDetails(List<ShiftTypeDto> absentShiftDetails) {
		this.absentShiftDetails = absentShiftDetails;
	}

	public List<AbsentDto> getAbsentEmpShiftDetails() {
		return absentEmpShiftDetails;
	}

	public void setAbsentEmpShiftDetails(List<AbsentDto> absentEmpShiftDetails) {
		this.absentEmpShiftDetails = absentEmpShiftDetails;
	}
	
	

}
