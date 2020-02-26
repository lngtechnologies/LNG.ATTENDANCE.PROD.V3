package com.lng.dto.reports;

public class EmpTodaysLeaveSummaryDto {

	private Integer approved;
	
	private Integer rejected;

	public Integer getApproved() {
		return approved;
	}

	public void setApproved(Integer approved) {
		this.approved = approved;
	}

	public Integer getRejected() {
		return rejected;
	}

	public void setRejected(Integer rejected) {
		this.rejected = rejected;
	}
}
