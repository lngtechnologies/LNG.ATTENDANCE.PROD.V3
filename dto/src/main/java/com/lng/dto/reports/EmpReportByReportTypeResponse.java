package com.lng.dto.reports;

import java.util.List;

import status.Status;

public class EmpReportByReportTypeResponse {

	private List<EmpReportByReportTypeDto> reportDto;
	
	public Status status;

	public List<EmpReportByReportTypeDto> getReportDto() {
		return reportDto;
	}

	public void setReportDto(List<EmpReportByReportTypeDto> reportDto) {
		this.reportDto = reportDto;
	}
}
