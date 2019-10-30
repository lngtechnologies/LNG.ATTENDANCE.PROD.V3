
package com.lng.dto.masters.state;



import java.util.List;

import status.Status;

public class StateResponse {

	public Status status; 
	public List<StateDto> data1;
	public  StateDto data;
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public List<StateDto> getData1() {
		return data1;
	}
	public void setData1(List<StateDto> data1) {
		this.data1 = data1;
	}
	public StateDto getData() {
		return data;
	}
	public void setData(StateDto data) {
		this.data = data;
	} 
	
	
}
