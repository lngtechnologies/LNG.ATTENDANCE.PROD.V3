package com.lng.dto.reports;

public class EmployeeTodaysSummaryDto {

	private Integer present;
	
	private Integer absent;
	
	private Integer leave;

	public Integer getPresent() {
		return present;
	}

	public void setPresent(Integer present) {
		this.present = present;
	}

	public Integer getAbsent() {
		return absent;
	}

	public void setAbsent(Integer absent) {
		this.absent = absent;
	}

	public Integer getLeave() {
		return leave;
	}

	public void setLeave(Integer leave) {
		this.leave = leave;
	}
}
