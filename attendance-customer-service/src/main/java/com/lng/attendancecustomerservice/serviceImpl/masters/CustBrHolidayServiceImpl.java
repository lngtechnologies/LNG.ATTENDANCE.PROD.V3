package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.Branch;
import com.lng.attendancecustomerservice.entity.masters.CustBrHoliday;
import com.lng.attendancecustomerservice.entity.masters.Department;
import com.lng.attendancecustomerservice.entity.masters.HolidayCalendar;
import com.lng.attendancecustomerservice.repositories.masters.BranchRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustBrHolidayRepository;
import com.lng.attendancecustomerservice.repositories.masters.HolidayCalendarRepository;
import com.lng.attendancecustomerservice.service.masters.CustBrHolidayService;
import com.lng.dto.masters.custBrHoliday.CustBrHolidayDto;
import com.lng.dto.masters.custBrHoliday.CustBrHolidayResponse;

import status.Status;
@Service
public class CustBrHolidayServiceImpl implements CustBrHolidayService {

	@Autowired
	CustBrHolidayRepository custBrHolidayRepository;

	@Autowired
	BranchRepository  branchRepository;

	@Autowired
	HolidayCalendarRepository   holidayCalendarRepository;

	/*
	 * @Override public CustBrHolidayResponse save(CustBrHolidayDto
	 * custBrHolidayDto) {
	 * 
	 * return null; }
	 */

	@Override
	public CustBrHolidayResponse saveCustBrHoliday(List<CustBrHolidayDto> custBrHolidayDtos) {
		CustBrHolidayResponse  custBrHolidayResponse  =  new  CustBrHolidayResponse();
		//CustBrHoliday  custBrHoliday = new CustBrHoliday();
		try {
			for(CustBrHolidayDto custBrHolidayDto : custBrHolidayDtos) {
				int  b = custBrHolidayRepository.findCustBrHolidayByRefbrId(custBrHolidayDto.getRefbrId());
				//if(b == 0 || b == 1) {
				int cust =	custBrHolidayRepository.findCustBrHolidayByBranch_BrIdAndHolidayCalendar_HolidayId(custBrHolidayDto.getRefbrId(), custBrHolidayDto.getRefHolidayId());
				if (cust == 0) {

					Branch branch1 =branchRepository.findBranchByBrId(custBrHolidayDto.getRefbrId());
					HolidayCalendar holidayCalendar = holidayCalendarRepository.findHolidayCalendarByHolidayId(custBrHolidayDto.getRefHolidayId());
					if(branch1 != null &&  holidayCalendar != null) {
						CustBrHoliday  custBrHoliday1 = new CustBrHoliday();
						custBrHoliday1.setBranch(branch1);
						custBrHoliday1.setHolidayCalendar(holidayCalendar);
						custBrHolidayRepository.save(custBrHoliday1);
						custBrHolidayResponse.status = new Status(false,200, "success");
					}else if(cust == 1) {
						custBrHolidayResponse.status = new Status(false,200, "success");
					}
					else {
						custBrHolidayResponse.status = new Status(false,200, "Customer And Branch MisMatch");
					}
				}

				else {

					List<CustBrHoliday> cust1 =custBrHolidayRepository.getCustBrHolidayByRefbrId(custBrHolidayDto.getRefbrId());
					if(cust1 != null) {
						custBrHolidayRepository.deleteAll(cust1);

						Branch branch1 =branchRepository.findBranchByBrId(custBrHolidayDto.getRefbrId());
						HolidayCalendar holidayCalendar = holidayCalendarRepository.findHolidayCalendarByHolidayId(custBrHolidayDto.getRefHolidayId());
						if(branch1 != null &&  holidayCalendar != null) {
							CustBrHoliday  custBrHoliday1 = new CustBrHoliday();
							custBrHoliday1.setBranch(branch1);
							custBrHoliday1.setHolidayCalendar(holidayCalendar);
							custBrHolidayRepository.save(custBrHoliday1);
							custBrHolidayResponse.status = new Status(false,200, "success");
						}
					}

				}
			}
		}catch(Exception ex){
			custBrHolidayResponse.status = new Status(true,500, "Something went wrong"); 
		}

		return custBrHolidayResponse;
	}
}
