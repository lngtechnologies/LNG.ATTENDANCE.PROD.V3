package com.lng.dto.masters.holidayCalendar;

import java.util.Date;

public class HolidayCalendarDto {
	
	private Integer holidayId;
	
	private Integer  refCustId;
	
	private String  holidayCalendarYear;
	
	private Date holidayDate;
	
	private String  holidayName;
	
	//private String  custName;
	
	private Integer  brId;
	
	private String brName;

	public Integer getHolidayId() {
		return holidayId;
	}

	public void setHolidayId(Integer holidayId) {
		this.holidayId = holidayId;
	}

	public Integer getRefCustId() {
		return refCustId;
	}

	public void setRefCustId(Integer refCustId) {
		this.refCustId = refCustId;
	}

	public String getHolidayCalendarYear() {
		return holidayCalendarYear;
	}

	public void setHolidayCalendarYear(String holidayCalendarYear) {
		this.holidayCalendarYear = holidayCalendarYear;
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

	public Integer getBrId() {
		return brId;
	}

	public void setBrId(Integer brId) {
		this.brId = brId;
	}

	public String getBrName() {
		return brName;
	}

	public void setBrName(String brName) {
		this.brName = brName;
	}

	
	
	
	

}

