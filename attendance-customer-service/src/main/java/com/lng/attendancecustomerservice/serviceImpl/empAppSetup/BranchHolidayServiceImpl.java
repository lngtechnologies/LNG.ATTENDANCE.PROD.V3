package com.lng.attendancecustomerservice.serviceImpl.empAppSetup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.repositories.empAppSetup.BranchHolidayRepository;
import com.lng.attendancecustomerservice.service.empAppSetup.BranchHolidayService;
import com.lng.dto.employeeAppSetup.BranchHolidayCalendarDto;
import com.lng.dto.employeeAppSetup.BranchHolidayCalendarResponse;

import status.Status;
@Service
public class BranchHolidayServiceImpl implements BranchHolidayService {

	@Autowired
	BranchHolidayRepository branchHolidayRepository;

	@Override
	public BranchHolidayCalendarResponse getHolidayListByBrId(Integer refbrId) {
		BranchHolidayCalendarResponse  branchHolidayCalendarResponse  =  new  BranchHolidayCalendarResponse();
		List<BranchHolidayCalendarDto> branchHolidayCalendarDtoList = new ArrayList<>();
		try {
			List<Object[]> holidayList =  branchHolidayRepository.findHolidayCalendarBybrId(refbrId);

			if(holidayList.isEmpty()) {
				branchHolidayCalendarResponse.status = new Status(false,400, " Not found");
			}else {
				for (Object[] p : holidayList) {	

					BranchHolidayCalendarDto branchHolidayCalendarDto = new BranchHolidayCalendarDto();
					Date date = ((Date)p[3]);
					SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
					branchHolidayCalendarDto.setDay(simpleDateformat.format(date));
					branchHolidayCalendarDto.setHolidayDate((Date)p[3]);
					branchHolidayCalendarDto.setHolidayName((p[4].toString()));
					branchHolidayCalendarDtoList.add(branchHolidayCalendarDto);
					branchHolidayCalendarResponse.setHolidayList(branchHolidayCalendarDtoList);
					branchHolidayCalendarResponse.status = new Status(false,200, "success");
				}

			}

		}catch (Exception e){
			branchHolidayCalendarResponse.status = new Status(true, 500, "Oops..! Something went wrong..");


		}
		return branchHolidayCalendarResponse;
	}

}
