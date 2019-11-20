package com.lng.dto.masters.employeeLeave;

import java.util.Date;

public class EmployeeLeaveDto {

	
	private Integer empLeaveId;
	
	private Integer empId;
	
	private Integer custLeaveId;
	
	private Date empLeaveAppliedDatetime;
	
	private Date empLeaveFrom;
	
	private Date empLeaveTo;
	
	private Integer empLeaveDaysCount;

	private String empLeaveRemarks;
	
	private String empLeaveStatus;
	
	private String empLeaveRejectionRemarks;
	
	private Integer empLeaveAppRejBy;
		
	private Date empLeaveStatusUpdatedDatetime;
	
	private Boolean empLeaveRequestForCancellation;

	public Integer getEmpLeaveId() {
		return empLeaveId;
	}

	public void setEmpLeaveId(Integer empLeaveId) {
		this.empLeaveId = empLeaveId;
	}
	public Integer getEmpId() {
		return empId;
	}

	public void setEmpId(Integer empId) {
		this.empId = empId;
	}

	public Integer getCustLeaveId() {
		return custLeaveId;
	}

	public void setCustLeaveId(Integer custLeaveId) {
		this.custLeaveId = custLeaveId;
	}

	public Date getEmpLeaveAppliedDatetime() {
		return empLeaveAppliedDatetime;
	}

	public void setEmpLeaveAppliedDatetime(Date empLeaveAppliedDatetime) {
		this.empLeaveAppliedDatetime = empLeaveAppliedDatetime;
	}

	public Date getEmpLeaveFrom() {
		return empLeaveFrom;
	}

	public void setEmpLeaveFrom(Date empLeaveFrom) {
		this.empLeaveFrom = empLeaveFrom;
	}

	public Date getEmpLeaveTo() {
		return empLeaveTo;
	}

	public void setEmpLeaveTo(Date empLeaveTo) {
		this.empLeaveTo = empLeaveTo;
	}

	public Integer getEmpLeaveDaysCount() {
		return empLeaveDaysCount;
	}

	public void setEmpLeaveDaysCount(Integer empLeaveDaysCount) {
		this.empLeaveDaysCount = empLeaveDaysCount;
	}

	public String getEmpLeaveRemarks() {
		return empLeaveRemarks;
	}

	public void setEmpLeaveRemarks(String empLeaveRemarks) {
		this.empLeaveRemarks = empLeaveRemarks;
	}

	public String getEmpLeaveStatus() {
		return empLeaveStatus;
	}

	public void setEmpLeaveStatus(String empLeaveStatus) {
		this.empLeaveStatus = empLeaveStatus;
	}

	public String getEmpLeaveRejectionRemarks() {
		return empLeaveRejectionRemarks;
	}

	public void setEmpLeaveRejectionRemarks(String empLeaveRejectionRemarks) {
		this.empLeaveRejectionRemarks = empLeaveRejectionRemarks;
	}

	public Integer getEmpLeaveAppRejBy() {
		return empLeaveAppRejBy;
	}

	public void setEmpLeaveAppRejBy(Integer empLeaveAppRejBy) {
		this.empLeaveAppRejBy = empLeaveAppRejBy;
	}

	public Date getEmpLeaveStatusUpdatedDatetime() {
		return empLeaveStatusUpdatedDatetime;
	}

	public void setEmpLeaveStatusUpdatedDatetime(Date empLeaveStatusUpdatedDatetime) {
		this.empLeaveStatusUpdatedDatetime = empLeaveStatusUpdatedDatetime;
	}

	public Boolean getEmpLeaveRequestForCancellation() {
		return empLeaveRequestForCancellation;
	}

	public void setEmpLeaveRequestForCancellation(Boolean empLeaveRequestForCancellation) {
		this.empLeaveRequestForCancellation = empLeaveRequestForCancellation;
	}

}
