package com.lng.attendancecustomerservice.entity.reports;

import java.util.List;

public class ReportResponseDto {
	public ReportDto key = new ReportDto();
	private List<ReportDto> data;
	private ReportMasterDataDto masterData;

	public List<ReportDto> getResult() {
		return data;
	}
	public void setResult(List<ReportDto> result) {
		this.data = result;
	}
	public ReportMasterDataDto getMasterData() {
		return masterData;
	}
	public void setMasterData(ReportMasterDataDto masterData) {
		this.masterData = masterData;
	}
}
