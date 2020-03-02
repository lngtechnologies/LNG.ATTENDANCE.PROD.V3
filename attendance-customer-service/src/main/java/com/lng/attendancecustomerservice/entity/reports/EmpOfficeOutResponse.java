package com.lng.attendancecustomerservice.entity.reports;

import java.util.List;

import status.Status;

public class EmpOfficeOutResponse {

	private MasterDataDto masterData;
	private List<EmpOfficeOutDto> officeOutDetails;
	
	public Status status;

	public List<EmpOfficeOutDto> getOfficeOutDetails() {
		return officeOutDetails;
	}

	public void setOfficeOutDetails(List<EmpOfficeOutDto> officeOutDetails) {
		this.officeOutDetails = officeOutDetails;
	}

	public MasterDataDto getMasterData() {
		return masterData;
	}

	public void setMasterData(MasterDataDto masterData) {
		this.masterData = masterData;
	}
}
