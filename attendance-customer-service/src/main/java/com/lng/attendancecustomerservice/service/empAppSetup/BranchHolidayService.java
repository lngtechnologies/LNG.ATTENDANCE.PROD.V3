package com.lng.attendancecustomerservice.service.empAppSetup;

import com.lng.dto.employeeAppSetup.BranchHolidayCalendarResponse;

public interface BranchHolidayService {
	
	BranchHolidayCalendarResponse getHolidayListByBrId(Integer refbrId);

}
