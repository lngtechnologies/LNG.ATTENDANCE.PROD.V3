package com.lng.dto.masters.shift;
import java.util.List;

import status.Status;

public class ShiftResponse {
	public Status status;
	public List<ShiftDto> data1;
	public ShiftDto data;
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public List<ShiftDto> getData1() {
		return data1;
	}
	public void setData1(List<ShiftDto> data1) {
		this.data1 = data1;
	}
	public ShiftDto getData() {
		return data;
	}
	public void setData(ShiftDto data) {
		this.data = data;
	}





}