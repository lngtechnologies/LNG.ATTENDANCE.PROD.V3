package com.lng.attendancecustomerservice.entity.reports;

import java.util.List;

import status.Status;

public class EmpEarlyLeaversAndLateComersResponse {

	private MasterDataDto masterData;
	private List<EmpEarlyLeaversDto> earlyLeavers;
	private List<EmpLateComersDto> lateComers;
	
	public Status status;

	public List<EmpEarlyLeaversDto> getEarlyLeavers() {
		return earlyLeavers;
	}

	public void setEarlyLeavers(List<EmpEarlyLeaversDto> earlyLeavers) {
		this.earlyLeavers = earlyLeavers;
	}

	public List<EmpLateComersDto> getLateComers() {
		return lateComers;
	}

	public void setLateComers(List<EmpLateComersDto> lateComers) {
		this.lateComers = lateComers;
	}

	public MasterDataDto getMasterData() {
		return masterData;
	}

	public void setMasterData(MasterDataDto masterData) {
		this.masterData = masterData;
	}
}
