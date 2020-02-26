package com.lng.dto.reports;

public class EmpTodaysEarlyLeaversDto {

	private Integer empId;
	
	private String empName;
	
	private String shiftEnd;
	
	private String attndOutDateTime;

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

	public String getShiftEnd() {
		return shiftEnd;
	}

	public void setShiftEnd(String shiftEnd) {
		this.shiftEnd = shiftEnd;
	}

	public String getAttndOutDateTime() {
		return attndOutDateTime;
	}

	public void setAttndOutDateTime(String attndOutDateTime) {
		this.attndOutDateTime = attndOutDateTime;
	}
}
