package com.lng.dto.masters.shift;
import java.util.List;

import status.Status;

public class ShiftResponse {
	public Status status;
	public List<ShiftDto> data;
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public List<ShiftDto> getData() {
		return data;
	}
	public void setData(List<ShiftDto> data) {
		this.data = data;
	}	
	
	

}
