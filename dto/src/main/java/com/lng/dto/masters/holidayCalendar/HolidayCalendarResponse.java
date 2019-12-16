package com.lng.dto.masters.holidayCalendar;

import java.util.List;
import status.Status;

public class HolidayCalendarResponse {



	public Status status;
	public List<HolidayCalendarDto> data1;
	public HolidayCalendarDto data;
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public List<HolidayCalendarDto> getData1() {
		return data1;
	}
	public void setData1(List<HolidayCalendarDto> data1) {
		this.data1 = data1;
	}
	public HolidayCalendarDto getData() {
		return data;
	}
	public void setData(HolidayCalendarDto data) {
		this.data = data;
	}



}
