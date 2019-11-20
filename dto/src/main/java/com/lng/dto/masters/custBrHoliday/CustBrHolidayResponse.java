package com.lng.dto.masters.custBrHoliday;

import java.util.List;

import status.Status;

public class CustBrHolidayResponse {
	
	public Status status;
	public List<CustBrHolidayDto> data1;
	public CustBrHolidayDto data;
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public List<CustBrHolidayDto> getData1() {
		return data1;
	}
	public void setData1(List<CustBrHolidayDto> data1) {
		this.data1 = data1;
	}
	public CustBrHolidayDto getData() {
		return data;
	}
	public void setData(CustBrHolidayDto data) {
		this.data = data;
	}
	

	

}
