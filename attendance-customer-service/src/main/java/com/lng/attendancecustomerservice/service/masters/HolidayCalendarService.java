package com.lng.attendancecustomerservice.service.masters;

import com.lng.dto.masters.holidayCalendar.HolidayCalendarDto;
import com.lng.dto.masters.holidayCalendar.HolidayCalendarResponse;

import status.Status;

public interface HolidayCalendarService {
	
	HolidayCalendarResponse getAllByRefCustId(Integer refCustId);
	HolidayCalendarResponse  save(HolidayCalendarDto  holidayCalendarDto);
	Status updateHolidayCalendarByHolidayId(HolidayCalendarDto  holidayCalendarDto);
	HolidayCalendarResponse deleteByHolidayId(Integer holidayId);
	HolidayCalendarResponse getHolidayCalendarByHolidayId(Integer holidayId);
	HolidayCalendarResponse  getAll();
	HolidayCalendarResponse findBranchList(Integer refCustId);
	HolidayCalendarResponse getHolidayCalendarByRefBrIdAndRefCustId(Integer refbrId,Integer refCustId);
	HolidayCalendarResponse getHolidayCalendarByRefCustId(Integer refCustId);
	HolidayCalendarResponse     getRemaingHoliday(Integer refCustId);
	HolidayCalendarResponse getHolidayCalendarByRefBrId(Integer refbrId);
	

}
