package com.lng.dto.reports;

import java.util.List;

import status.Status;

public class TodaysLateComersAndEarlyLeaversResponse {

	private List<EmpTodaysLateComersDto> lateComers;
	
	private List<EmpTodaysEarlyLeaversDto> earlyLeavers;
	
	public Status status;

	public List<EmpTodaysLateComersDto> getLateComers() {
		return lateComers;
	}

	public void setLateComers(List<EmpTodaysLateComersDto> lateComers) {
		this.lateComers = lateComers;
	}

	public List<EmpTodaysEarlyLeaversDto> getEarlyLeavers() {
		return earlyLeavers;
	}

	public void setEarlyLeavers(List<EmpTodaysEarlyLeaversDto> earlyLeavers) {
		this.earlyLeavers = earlyLeavers;
	}
}
