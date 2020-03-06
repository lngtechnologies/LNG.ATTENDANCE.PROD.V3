package com.lng.attendancecustomerservice.entity.reports;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmployeeSummaryParamDto {
	
	@JsonProperty("Name")
	private String empName;
	
	@JsonProperty("Mobile No")
	private String mobileNo;
	
	@JsonProperty("Department")
	private String deptName;
	
	@JsonProperty("Branch")
	private String branchName;

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	
	

}
