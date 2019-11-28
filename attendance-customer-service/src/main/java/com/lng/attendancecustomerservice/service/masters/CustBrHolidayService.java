package com.lng.attendancecustomerservice.service.masters;

import java.util.List;

import com.lng.dto.masters.custBrHoliday.CustBrHolidayDto;
import com.lng.dto.masters.custBrHoliday.CustBrHolidayResponse;

import status.Status;

public interface CustBrHolidayService {
	
	CustBrHolidayResponse   save(List<CustBrHolidayDto> custBrHolidayDto);
	CustBrHolidayResponse  saveCustBrHoliday(List<CustBrHolidayDto> custBrHolidayDto);
	CustBrHolidayResponse    delete(Integer custBrHolidayId);
	Status updateCustBrHoliday(CustBrHolidayDto custBrHolidayDto);
	CustBrHolidayResponse    getCustBrHolidayByCustBrHolidayId(Integer custBrHolidayId);
	
}
