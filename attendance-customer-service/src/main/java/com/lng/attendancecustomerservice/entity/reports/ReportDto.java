package com.lng.attendancecustomerservice.entity.reports;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportDto {
	@JsonProperty("Name")
	private String name;

	@JsonProperty("Designation")
	private String designation;

	@JsonProperty("Block")
	private String block;

	@JsonProperty("Shift")
	private String shift;

	@JsonProperty("In")
	private String timeIn;

	@JsonProperty("In Location")
	private String timeInLocation;

	@JsonProperty("Out")
	private String timeOut;

	@JsonProperty("Out Location")
	private String timeOutLocation;

	@JsonProperty("Approved GEO Location")
	private String approvedGeoLocation;

	@JsonProperty("Worked Hrs")
	private String workedHrs;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(String block) {
		this.block = block;
	}

	public String getShift() {
		return shift;
	}

	public void setShift(String shift) {
		this.shift = shift;
	}

	public String getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(String timeIn) {
		this.timeIn = timeIn;
	}

	public String getTimeInLocation() {
		return timeInLocation;
	}

	public void setTimeInLocation(String timeInLocation) {
		this.timeInLocation = timeInLocation;
	}

	public String getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(String timeOut) {
		this.timeOut = timeOut;
	}

	public String getTimeOutLocation() {
		return timeOutLocation;
	}

	public void setTimeOutLocation(String timeOutLocation) {
		this.timeOutLocation = timeOutLocation;
	}

	public String getApprovedGeoLocation() {
		return approvedGeoLocation;
	}

	public void setApprovedGeoLocation(String approvedGeoLocation) {
		this.approvedGeoLocation = approvedGeoLocation;
	}

	public String getWorkedHrs() {
		return workedHrs;
	}

	public void setWorkedHrs(String workedHrs) {
		this.workedHrs = workedHrs;
	}
}
