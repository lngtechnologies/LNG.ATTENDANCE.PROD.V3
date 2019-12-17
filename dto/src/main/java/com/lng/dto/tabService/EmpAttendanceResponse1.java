package com.lng.dto.tabService;

import java.util.List;

import status.Status;

public class EmpAttendanceResponse1 {

	public List<EmpAttendanceDto1> data1;

	public Status status;

	public List<EmpAttendanceDto1> getData1() {
		return data1;
	}

	public void setData1(List<EmpAttendanceDto1> data1) {
		this.data1 = data1;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}



}
