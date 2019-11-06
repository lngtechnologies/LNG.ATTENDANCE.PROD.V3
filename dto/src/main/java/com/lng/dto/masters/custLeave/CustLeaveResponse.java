package com.lng.dto.masters.custLeave;

import java.util.List;


import status.Status;

public class CustLeaveResponse {
	
	public Status status;
	
	public List<custLeaveDto> data1;
	
	public custLeaveDto data;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<custLeaveDto> getData1() {
		return data1;
	}

	public void setData1(List<custLeaveDto> data1) {
		this.data1 = data1;
	}

	public custLeaveDto getData() {
		return data;
	}

	public void setData(custLeaveDto data) {
		this.data = data;
	}
	
	

}
