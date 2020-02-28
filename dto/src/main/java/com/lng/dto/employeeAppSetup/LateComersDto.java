package com.lng.dto.employeeAppSetup;

public class LateComersDto {
	
	private String    empName;
	
	private  String  shiftName;

	private String   shiftStart;

	private String   shiftEnd;

	private  String   attndInTime;

	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}

	public String getShiftStart() {
		return shiftStart;
	}

	public void setShiftStart(String shiftStart) {
		this.shiftStart = shiftStart;
	}

	public String getShiftEnd() {
		return shiftEnd;
	}

	public void setShiftEnd(String shiftEnd) {
		this.shiftEnd = shiftEnd;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getAttndInTime() {
		return attndInTime;
	}

	public void setAttndInTime(String attndInTime) {
		this.attndInTime = attndInTime;
	}




}
