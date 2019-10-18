
package com.lng.dto.state;



import java.util.List;

import status.Status;

public class StateResponse {

	public Status status; 
	public List<StateDto> data;
	
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public List<StateDto> getData() {
		return data;
	}
	public void setData(List<StateDto> data) {
		this.data = data;
	}



}
