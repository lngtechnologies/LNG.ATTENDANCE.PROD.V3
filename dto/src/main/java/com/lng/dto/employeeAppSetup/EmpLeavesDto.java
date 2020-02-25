package com.lng.dto.employeeAppSetup;

import java.util.Date;

public class EmpLeavesDto {

	private Integer empId;

	private String empName;

	private Date empLeaveFrom;

	private Date empLeaveTo;

	private Integer empLeaveDaysCount;

	private String empLeaveStatus;

	private String empLeaveRemarks;

	private String  leaveType;

	private Integer custId;
	
	private Integer empLeaveId;

	public Integer getEmpId() {
		return empId;
	}

	public void setEmpId(Integer empId) {
		this.empId = empId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
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

	public String getEmpLeaveStatus() {
		return empLeaveStatus;
	}

	public void setEmpLeaveStatus(String empLeaveStatus) {
		this.empLeaveStatus = empLeaveStatus;
	}

	public String getEmpLeaveRemarks() {
		return empLeaveRemarks;
	}

	public void setEmpLeaveRemarks(String empLeaveRemarks) {
		this.empLeaveRemarks = empLeaveRemarks;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}

	public Integer getEmpLeaveId() {
		return empLeaveId;
	}

	public void setEmpLeaveId(Integer empLeaveId) {
		this.empLeaveId = empLeaveId;
	}



}
