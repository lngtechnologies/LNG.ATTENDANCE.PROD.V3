package com.lng.attendancecustomerservice.controllers.empAppSetup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.empAppSetup.BranchHolidayService;
import com.lng.dto.employeeAppSetup.BranchHolidayCalendarResponse;
import com.lng.dto.masters.holidayCalendar.HolidayCalendarDto;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value="/mobile/holidayClaendar")
public class BranchHolidayController {

	@Autowired
	BranchHolidayService branchHolidayService;

	@PostMapping(value = "/getholidayListByBrId")
	public ResponseEntity<BranchHolidayCalendarResponse> getByBrId(@RequestBody HolidayCalendarDto holidayCalendarDto){
		BranchHolidayCalendarResponse branchHolidayCalendarResponse  = branchHolidayService.getHolidayListByBrId(holidayCalendarDto.getRefbrId());
		if(branchHolidayCalendarResponse != null) {
			return new ResponseEntity<BranchHolidayCalendarResponse>(branchHolidayCalendarResponse,HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}


}
