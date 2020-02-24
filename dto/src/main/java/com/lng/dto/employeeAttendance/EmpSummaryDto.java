package com.lng.dto.employeeAttendance;

public class EmpSummaryDto {

	private Integer custId;

	private Integer loginId;
	
	private Integer empId;
	
	private Integer present;
	
	private Integer absent;
	
	private Integer  totalLeave;

	
	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}

	public Integer getLoginId() {
		return loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}

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

	public Integer getTotalLeave() {
		return totalLeave;
	}

	public void setTotalLeave(Integer totalLeave) {
		this.totalLeave = totalLeave;
	}

	public Integer getEmpId() {
		return empId;
	}

	public void setEmpId(Integer empId) {
		this.empId = empId;
	}
	
}
