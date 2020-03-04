package com.lng.dto.employeeAppSetup;

import java.util.List;

import status.Status;

public class BranchHolidayCalendarResponse {
	
	public Status status;
	
	public List<BranchHolidayCalendarDto> holidayList;

	public List<BranchHolidayCalendarDto> getHolidayList() {
		return holidayList;
	}

	public void setHolidayList(List<BranchHolidayCalendarDto> holidayList) {
		this.holidayList = holidayList;
	}
	
	

}
