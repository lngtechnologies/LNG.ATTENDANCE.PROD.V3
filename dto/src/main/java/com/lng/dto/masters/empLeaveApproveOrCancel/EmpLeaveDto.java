package com.lng.dto.masters.empLeaveApproveOrCancel;

import java.util.Date;

public class EmpLeaveDto {

	private Integer empLeaveId;
	
	private Integer loginId;
	
	private Integer custId;
	
	private Integer empId;
	
	private String empName;
	
	private Integer deptId;
	
	private String deptName;
	
	private Date empLeaveFrom;
	
	private Date empLeaveTo;
	
	private Integer empLeaveDaysCount;
	
	private String empLeaveStatus;
	
	private String empLeaveRemarks;
	
	

	public String getEmpLeaveRemarks() {
		return empLeaveRemarks;
	}

	public void setEmpLeaveRemarks(String empLeaveRemarks) {
		this.empLeaveRemarks = empLeaveRemarks;
	}

	public Integer getEmpLeaveId() {
		return empLeaveId;
	}

	public void setEmpLeaveId(Integer empLeaveId) {
		this.empLeaveId = empLeaveId;
	}

	public Integer getLoginId() {
		return loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}

	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}

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

	public Integer getDeptId() {
		return deptId;
	}

	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
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

}
