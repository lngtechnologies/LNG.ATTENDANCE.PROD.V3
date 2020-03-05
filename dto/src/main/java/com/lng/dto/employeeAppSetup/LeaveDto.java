package com.lng.dto.employeeAppSetup;

public class LeaveDto {

	private Integer empId;

	private String empName;

	private String shiftName;

	private String empLeaveFrom;

	private String empLeaveTo;

	private Integer empLeaveDaysCount;

	private  String  empLeaveStatus;

	private  String   empLeaveRemarks;
	
	private String    leaveType;

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

	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}


	public String getEmpLeaveFrom() {
		return empLeaveFrom;
	}

	public void setEmpLeaveFrom(String empLeaveFrom) {
		this.empLeaveFrom = empLeaveFrom;
	}

	public String getEmpLeaveTo() {
		return empLeaveTo;
	}

	public void setEmpLeaveTo(String empLeaveTo) {
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

}
