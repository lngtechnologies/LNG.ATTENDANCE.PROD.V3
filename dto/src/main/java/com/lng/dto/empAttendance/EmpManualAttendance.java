package com.lng.dto.empAttendance;

import java.util.List;

public class EmpManualAttendance {

	private List<EmpAttendanceInDto> attendanceInDetails;
	
	private List<EmpAttendaceOutDto> attendanceOutDetails;

	public List<EmpAttendanceInDto> getAttendanceInDetails() {
		return attendanceInDetails;
	}

	public void setAttendanceInDetails(List<EmpAttendanceInDto> attendanceInDetails) {
		this.attendanceInDetails = attendanceInDetails;
	}

	public List<EmpAttendaceOutDto> getAttendanceOutDetails() {
		return attendanceOutDetails;
	}

	public void setAttendanceOutDetails(List<EmpAttendaceOutDto> attendanceOutDetails) {
		this.attendanceOutDetails = attendanceOutDetails;
	}
}
