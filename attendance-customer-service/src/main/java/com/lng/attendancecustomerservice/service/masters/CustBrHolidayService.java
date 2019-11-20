package com.lng.attendancecustomerservice.service.masters;

import java.util.List;

import com.lng.dto.masters.custBrHoliday.CustBrHolidayDto;
import com.lng.dto.masters.custBrHoliday.CustBrHolidayResponse;

public interface CustBrHolidayService {
	
	//CustBrHolidayResponse   save(CustBrHolidayDto custBrHolidayDto);
	CustBrHolidayResponse  saveCustBrHoliday(List<CustBrHolidayDto> custBrHolidayDto);

}
