package com.lng.dto.reports;

public class EmpTodaysLateComersDto {

	private Integer empId;
	
	private String empName;
	
	private String shiftStart;
	
	private String attndInDateTime;

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

	public String getShiftStart() {
		return shiftStart;
	}

	public void setShiftStart(String shiftStart) {
		this.shiftStart = shiftStart;
	}

	public String getAttndInDateTime() {
		return attndInDateTime;
	}

	public void setAttndInDateTime(String attndInDateTime) {
		this.attndInDateTime = attndInDateTime;
	}
}
