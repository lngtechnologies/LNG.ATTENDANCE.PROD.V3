package com.lng.attendancecustomerservice.entity.reports;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportEmployeeSummaryDto {
	
	@JsonProperty("Date")
	private String date;
	
	@JsonProperty("Shift")
	private String shift;
	
	@JsonProperty("In")
	private String timeIn;
	
	@JsonProperty("Out")
	private String timeOut;
	
	@JsonProperty("Worked Hrs")
	private String workedHrs;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Approved GEO Location")
	private String approvedGeoLocation;

	@JsonProperty("In Location")
	private String timeInLocation;

	@JsonProperty("Out Location")
	private String timeOutLocation;
	
	@JsonProperty("In Address")
	private String  inAddress;
	
	@JsonProperty("Out Address")
	private String  outAddress;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public String getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(String timeOut) {
		this.timeOut = timeOut;
	}

	public String getWorkedHrs() {
		return workedHrs;
	}

	public void setWorkedHrs(String workedHrs) {
		this.workedHrs = workedHrs;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getApprovedGeoLocation() {
		return approvedGeoLocation;
	}

	public void setApprovedGeoLocation(String approvedGeoLocation) {
		this.approvedGeoLocation = approvedGeoLocation;
	}

	public String getTimeInLocation() {
		return timeInLocation;
	}

	public void setTimeInLocation(String timeInLocation) {
		this.timeInLocation = timeInLocation;
	}

	public String getTimeOutLocation() {
		return timeOutLocation;
	}

	public void setTimeOutLocation(String timeOutLocation) {
		this.timeOutLocation = timeOutLocation;
	}

	public String getInAddress() {
		return inAddress;
	}

	public void setInAddress(String inAddress) {
		this.inAddress = inAddress;
	}

	public String getOutAddress() {
		return outAddress;
	}

	public void setOutAddress(String outAddress) {
		this.outAddress = outAddress;
	}
	

}
