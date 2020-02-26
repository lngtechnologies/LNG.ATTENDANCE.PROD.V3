package com.lng.dto.employeeAttendance;

public class EmpAttendanceSumaryDto {

	private Integer present;

	private Integer absent;

	private Integer  totalAppLeave;

	private Integer   totalPendingLeave;

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

	public Integer getTotalAppLeave() {
		return totalAppLeave;
	}

	public void setTotalAppLeave(Integer totalAppLeave) {
		this.totalAppLeave = totalAppLeave;
	}

	public Integer getTotalPendingLeave() {
		return totalPendingLeave;
	}

	public void setTotalPendingLeave(Integer totalPendingLeave) {
		this.totalPendingLeave = totalPendingLeave;
	}

}
