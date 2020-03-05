package com.lng.dto.reports;

public class EmployeeTodaysSummaryDto {

	private Integer present;
	
	private Integer absent;
	
	private Integer approvedLeaves;
	
	private Integer pendingLeaves;

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

	public Integer getApprovedLeave() {
		return approvedLeaves;
	}

	public void setApprovedLeave(Integer approvedLeave) {
		this.approvedLeaves = approvedLeave;
	}

	public Integer getPendingLeaves() {
		return pendingLeaves;
	}

	public void setPendingLeaves(Integer pendingLeaves) {
		this.pendingLeaves = pendingLeaves;
	}

}
