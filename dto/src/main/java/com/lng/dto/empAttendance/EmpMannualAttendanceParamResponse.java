package com.lng.dto.empAttendance;

import java.util.List;

import status.Status;

public class EmpMannualAttendanceParamResponse {
	
	public Status status; 
	public List<EmpManualAttendanceParamDto> data2;
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public List<EmpManualAttendanceParamDto> getData2() {
		return data2;
	}
	public void setData2(List<EmpManualAttendanceParamDto> data2) {
		this.data2 = data2;
	}
	
	
	

}
