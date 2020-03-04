package com.lng.dto.employeeAppSetup;

import java.util.Date;

public class BranchHolidayCalendarDto {

	private  String day;

	private  Date holidayDate;

	private String holidayName;

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
	public Date getHolidayDate() {
		return holidayDate;
	}

	public void setHolidayDate(Date holidayDate) {
		this.holidayDate = holidayDate;
	}

	public String getHolidayName() {
		return holidayName;
	}

	public void setHolidayName(String holidayName) {
		this.holidayName = holidayName;
	}



}
