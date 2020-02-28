package com.lng.attendancecustomerservice.entity.reports;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmpOfficeOutDto {

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

	@JsonProperty("Designated Location")
	private String designatedLocation;

	@JsonProperty("Attendance Out Location")
	private String attendanceOutLocation;

	@JsonProperty("Attendance Date")
	private String attendanceDate;

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

	public String getDesignatedLocation() {
		return designatedLocation;
	}

	public void setDesignatedLocation(String designatedLocation) {
		this.designatedLocation = designatedLocation;
	}

	public String getAttendanceOutLocation() {
		return attendanceOutLocation;
	}

	public void setAttendanceOutLocation(String attendanceOutLocation) {
		this.attendanceOutLocation = attendanceOutLocation;
	}

	public String getAttendanceDate() {
		return attendanceDate;
	}

	public void setAttendanceDate(String attendanceDate) {
		this.attendanceDate = attendanceDate;
	}
}
