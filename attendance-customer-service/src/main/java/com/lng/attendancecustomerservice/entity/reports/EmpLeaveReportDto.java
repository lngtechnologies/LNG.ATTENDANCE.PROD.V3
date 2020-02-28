package com.lng.attendancecustomerservice.entity.reports;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmpLeaveReportDto {

	@JsonProperty("Name")
	private String empName;
	
	@JsonProperty("Department")
	private String deptName;
	
	@JsonProperty("Branch")
	private String branchName;
	
	@JsonProperty("Block")
	private String blockName;
	
	@JsonProperty("Designation")
	private String designationName;
	
	@JsonProperty("Shift")
	private String shiftName;


	@JsonProperty("Leave Date from")
	private String leaveFrom;

	@JsonProperty("Leave Date to")
	private String leaveTo;

	@JsonProperty("No Of Days")
	private String noOfDays;

	@JsonProperty("Status")
	private String status;

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
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

	public String getBlockName() {
		return blockName;
	}

	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}

	public String getDesignationName() {
		return designationName;
	}

	public void setDesignationName(String designationName) {
		this.designationName = designationName;
	}

	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}

	public String getLeaveFrom() {
		return leaveFrom;
	}

	public void setLeaveFrom(String leaveFrom) {
		this.leaveFrom = leaveFrom;
	}

	public String getLeaveTo() {
		return leaveTo;
	}

	public void setLeaveTo(String leaveTo) {
		this.leaveTo = leaveTo;
	}

	public String getNoOfDays() {
		return noOfDays;
	}

	public void setNoOfDays(String noOfDays) {
		this.noOfDays = noOfDays;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
