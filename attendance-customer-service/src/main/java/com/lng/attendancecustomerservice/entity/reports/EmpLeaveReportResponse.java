package com.lng.attendancecustomerservice.entity.reports;

import java.util.List;

import status.Status;

public class EmpLeaveReportResponse {

	private MasterDataDto masterData;
	private List<EmpLeaveReportDto> leaveReport;
	
	public Status status;

	public List<EmpLeaveReportDto> getLeaveReport() {
		return leaveReport;
	}

	public void setLeaveReport(List<EmpLeaveReportDto> leaveReport) {
		this.leaveReport = leaveReport;
	}

	public MasterDataDto getMasterData() {
		return masterData;
	}

	public void setMasterData(MasterDataDto masterData) {
		this.masterData = masterData;
	}
}
