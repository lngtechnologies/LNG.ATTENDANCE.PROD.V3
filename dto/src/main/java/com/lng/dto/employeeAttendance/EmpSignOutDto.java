package com.lng.dto.employeeAttendance;

public class EmpSignOutDto {

	private Integer empId;
	
	private String flag;
	
	private String attendanceInDateTime;

	public Integer getEmpId() {
		return empId;
	}

	public void setEmpId(Integer empId) {
		this.empId = empId;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getAttendanceInDateTime() {
		return attendanceInDateTime;
	}

	public void setAttendanceInDateTime(String attendanceInDateTime) {
		this.attendanceInDateTime = attendanceInDateTime;
	}

	
}
