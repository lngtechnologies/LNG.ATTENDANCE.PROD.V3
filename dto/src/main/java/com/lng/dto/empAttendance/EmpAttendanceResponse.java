package com.lng.dto.empAttendance;


import java.util.List;


import status.Status;

public class EmpAttendanceResponse {

	public Status status; 
	public EmpAttendanceParamDto data;
	public List<EmpAttendanceParamDto> data1;

	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public EmpAttendanceParamDto getData() {
		return data;
	}
	public void setData(EmpAttendanceParamDto data) {
		this.data = data;
	}
	public List<EmpAttendanceParamDto> getData1() {
		return data1;
	}
	public void setData1(List<EmpAttendanceParamDto> data1) {
		this.data1 = data1;
	}

	
	
}
