package com.lng.attendancecustomerservice.entity.reports;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MasterDataDto {

	@JsonProperty("Customer Name")
	private String custName;

	@JsonProperty("Customer Address")
	private String custAddress;
	
	@JsonProperty("Branch Address")
	private String brAddress;
	
	@JsonProperty("Department")
	private String deptName;
	
	@JsonProperty("Branch")
	private String brName;
	
	@JsonProperty("Date")
	private String date;
	
	@JsonProperty("From Date")
	private Date fromDate;
	
	@JsonProperty("To Date")
	private Date toDate;
	
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getBrAddress() {
		return brAddress;
	}
	public void setBrAddress(String brAddress) {
		this.brAddress = brAddress;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getBrName() {
		return brName;
	}
	public void setBrName(String brName) {
		this.brName = brName;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public String getCustAddress() {
		return custAddress;
	}
	public void setCustAddress(String custAddress) {
		this.custAddress = custAddress;
	}
}
