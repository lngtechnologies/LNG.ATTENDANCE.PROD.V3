package com.lng.attendancecustomerservice.entity.reports;

import java.util.List;

public class ResponseSummaryReport {
	private List<ReportEmployeeSummaryDto> data;
	private ReportMasterDataDto masterData;

	public List<ReportEmployeeSummaryDto> getResult() {
		return data;
	}
	public void setResult(List<ReportEmployeeSummaryDto> result) {
		this.data = result;
	}
	public ReportMasterDataDto getMasterData() {
		return masterData;
	}
	public void setMasterData(ReportMasterDataDto masterData) {
		this.masterData = masterData;
	}
}
