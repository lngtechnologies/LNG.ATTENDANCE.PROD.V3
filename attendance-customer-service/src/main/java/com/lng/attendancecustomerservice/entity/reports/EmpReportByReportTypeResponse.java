package com.lng.attendancecustomerservice.entity.reports;

import java.util.List;

import status.Status;

public class EmpReportByReportTypeResponse {

	private MasterDataDto masterData;
	private List<EmpReportByReportTypeDto> reportDto;
	
	public Status status;

	public List<EmpReportByReportTypeDto> getReportDto() {
		return reportDto;
	}

	public void setReportDto(List<EmpReportByReportTypeDto> reportDto) {
		this.reportDto = reportDto;
	}

	public MasterDataDto getMasterData() {
		return masterData;
	}

	public void setMasterData(MasterDataDto masterData) {
		this.masterData = masterData;
	}
}
