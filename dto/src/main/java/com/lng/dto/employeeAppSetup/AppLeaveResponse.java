package com.lng.dto.employeeAppSetup;

import java.util.List;

import status.Status;

public class AppLeaveResponse {
	
	private  List<ShiftTypeDto>   appLeaveShiftDetails;

	private  List<LeaveDto>      appLeaveEmpShiftDetails;

	public Status status;

	public List<ShiftTypeDto> getAppLeaveShiftDetails() {
		return appLeaveShiftDetails;
	}

	public void setAppLeaveShiftDetails(List<ShiftTypeDto> appLeaveShiftDetails) {
		this.appLeaveShiftDetails = appLeaveShiftDetails;
	}

	public List<LeaveDto> getAppLeaveEmpShiftDetails() {
		return appLeaveEmpShiftDetails;
	}

	public void setAppLeaveEmpShiftDetails(List<LeaveDto> appLeaveEmpShiftDetails) {
		this.appLeaveEmpShiftDetails = appLeaveEmpShiftDetails;
	}
	
	
	
	

}
