package com.lng.dto.empMovement;

import java.util.List;

import status.Status;

public class EmpMovementResponse {
	
	public Status  status;
	
	public  List<EmpMovementDto> data1;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<EmpMovementDto> getData1() {
		return data1;
	}

	public void setData1(List<EmpMovementDto> data1) {
		this.data1 = data1;
	}
	
	

}
