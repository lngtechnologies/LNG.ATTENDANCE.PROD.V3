package com.lng.attendancecustomerservice.entity.reports;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmpLateComersDto {

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
	
	@JsonProperty("Shift Start Time")
	private String shiftStart;
	
	@JsonProperty("Actual Time in")
	private String inTime;
	
	@JsonProperty("Late by")
	private String diff;

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

	public String getShiftStart() {
		return shiftStart;
	}

	public void setShiftStart(String shiftStart) {
		this.shiftStart = shiftStart;
	}

	public String getInTime() {
		return inTime;
	}

	public void setInTime(String inTime) {
		this.inTime = inTime;
	}

	public String getDiff() {
		return diff;
	}

	public void setDiff(String diff) {
		this.diff = diff;
	}
}
