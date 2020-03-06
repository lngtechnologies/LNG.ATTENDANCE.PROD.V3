package com.lng.attendancecustomerservice.entity.reports;

import java.util.List;

import status.Status;

public class SummaryDetailsResponse {
	
	private List<EmployeeSummaryParamDto> presentList;
	
	private List<EmployeeSummaryParamDto> absentList;
	
	private List<EmployeeSummaryParamDto> approvedList;
	
	private List<EmployeeSummaryParamDto> pendingList;
	
	public Status status;

	public List<EmployeeSummaryParamDto> getPresentList() {
		return presentList;
	}

	public void setPresentList(List<EmployeeSummaryParamDto> presentList) {
		this.presentList = presentList;
	}

	public List<EmployeeSummaryParamDto> getAbsentList() {
		return absentList;
	}

	public void setAbsentList(List<EmployeeSummaryParamDto> absentList) {
		this.absentList = absentList;
	}

	public List<EmployeeSummaryParamDto> getApprovedList() {
		return approvedList;
	}

	public void setApprovedList(List<EmployeeSummaryParamDto> approvedList) {
		this.approvedList = approvedList;
	}

	public List<EmployeeSummaryParamDto> getPendingList() {
		return pendingList;
	}

	public void setPendingList(List<EmployeeSummaryParamDto> pendingList) {
		this.pendingList = pendingList;
	}
	
}
