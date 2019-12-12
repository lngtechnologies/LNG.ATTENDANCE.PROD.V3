package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.Branch;
import com.lng.attendancecustomerservice.entity.masters.CustBrHoliday;
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

	ModelMapper modelMapper=new ModelMapper();
	@Autowired
	CustBrHolidayRepository custBrHolidayRepository;

	@Autowired
	BranchRepository  branchRepository;

	@Autowired
	HolidayCalendarRepository   holidayCalendarRepository;

	@Override
	public CustBrHolidayResponse saveCustBrHoliday(List<CustBrHolidayDto> custBrHolidayDtos) {
		CustBrHolidayResponse  custBrHolidayResponse  =  new  CustBrHolidayResponse();
		try {
			for(CustBrHolidayDto custBrHolidayDto : custBrHolidayDtos) {

				Branch branch1 =branchRepository.findBranchByBrId(custBrHolidayDto.getRefbrId());
				HolidayCalendar holidayCalendar = holidayCalendarRepository.findHolidayCalendarByHolidayId(custBrHolidayDto.getRefHolidayId());
				if(branch1 != null &&  holidayCalendar != null) {
					CustBrHoliday cH = custBrHolidayRepository.findCustBrHolidayByBranch_brIdAndHolidayCalendar_HolidayId(custBrHolidayDto.getRefbrId(), custBrHolidayDto.getRefHolidayId());
					if(cH == null) {
						CustBrHoliday  custBrHoliday1 = new CustBrHoliday();
						custBrHoliday1.setBranch(branch1);
						custBrHoliday1.setHolidayCalendar(holidayCalendar);
						custBrHolidayRepository.save(custBrHoliday1);
						custBrHolidayResponse.status = new Status(false,200, "successfully created");
					}else {
						custBrHolidayResponse.status = new Status(true,400, "Holiday name already exists");

					}
				}else {
					custBrHolidayResponse.status = new Status(false,400, "Not found");
				}
			}

		}catch(Exception ex){
			custBrHolidayResponse.status = new Status(true,500, "Something went wrong"); 
		}

		return custBrHolidayResponse;
	}

	@Override
	public CustBrHolidayResponse delete(Integer custBrHolidayId) {
		CustBrHolidayResponse  custBrHolidayResponse  =  new  CustBrHolidayResponse();
		try {
			CustBrHoliday  custBrHoliday = custBrHolidayRepository.findCustBrHolidayByCustBrHolidayId(custBrHolidayId);
			if(custBrHoliday != null) {
				custBrHolidayRepository.delete(custBrHoliday);	
				custBrHolidayResponse.status = new Status(false,200, "successfully deleted");
			}else {
				custBrHolidayResponse.status = new Status(false,400, "Not found");	
			}

		}catch(Exception ex){
			custBrHolidayResponse.status = new Status(true,500, "Something went wrong"); 
		}

		return custBrHolidayResponse;
	}
	@Override
	public CustBrHolidayResponse save(List<CustBrHolidayDto> custBrHolidayDtos) {
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
						custBrHolidayResponse.status = new Status(true,400, "Customer and branch misMatch");
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

	@Override
	public Status updateCustBrHoliday(CustBrHolidayDto custBrHolidayDto) {

		Status status = null;
		try {

			CustBrHoliday CustBrHoliday = custBrHolidayRepository.findCustBrHolidayByCustBrHolidayId(custBrHolidayDto.getCustBrHolidayId());
			Branch branch = branchRepository.findBranchByBrId(custBrHolidayDto.getRefbrId());
			HolidayCalendar holidayCalendar  =  holidayCalendarRepository.findHolidayCalendarByHolidayId(custBrHolidayDto.getRefHolidayId());
			if(branch != null && holidayCalendar != null ) {
				CustBrHoliday cH = custBrHolidayRepository.findCustBrHolidayByBranch_brIdAndHolidayCalendar_HolidayId(custBrHolidayDto.getRefbrId(), custBrHolidayDto.getRefHolidayId());
				if(cH == null) {
					CustBrHoliday = modelMapper.map(custBrHolidayDto,CustBrHoliday.class);
					CustBrHoliday.setBranch(branch);
					CustBrHoliday.setHolidayCalendar(holidayCalendar);
					custBrHolidayRepository.save(CustBrHoliday);
					status = new Status(false, 200, "successfully updated");

				}else {
					status = new Status(true,400,"Holiday already exists");

				}
			}
			else {
				status = new Status(false, 400, "Not Found");

			}
		}
		catch(Exception e) {
			status = new Status(true, 4000, e.getMessage());
		}
		return status;
	}

	@Override
	public CustBrHolidayResponse getCustBrHolidayByCustBrHolidayId(Integer custBrHolidayId) {
		CustBrHolidayResponse  custBrHolidayResponse  =  new  CustBrHolidayResponse();
		try {
			CustBrHoliday  custBrHoliday  =  custBrHolidayRepository.findCustBrHolidayByCustBrHolidayId(custBrHolidayId);
			if(custBrHoliday != null) {
				CustBrHolidayDto  custBrHolidayDto = convertToCustBrHolidayDtoDto(custBrHoliday);
				custBrHolidayResponse.data = custBrHolidayDto;
				custBrHolidayResponse.status = new Status(false,200, "Success");
			}
			else {
				custBrHolidayResponse.status = new Status(false, 400, "Not found");
			}
		}catch(Exception e) {
			custBrHolidayResponse.status = new Status(true,3000, e.getMessage()); 

		}
		return custBrHolidayResponse;
	}
	public CustBrHolidayDto convertToCustBrHolidayDtoDto(CustBrHoliday custBrHoliday) {
		CustBrHolidayDto custBrHolidayDto = modelMapper.map(custBrHoliday,CustBrHolidayDto.class);
		custBrHolidayDto.setRefbrId(custBrHoliday.getBranch().getBrId());
		custBrHolidayDto.setBrName(custBrHoliday.getBranch().getBrName());
		custBrHolidayDto.setRefHolidayId(custBrHoliday.getHolidayCalendar().getHolidayId());
		custBrHolidayDto.setHolidayName(custBrHoliday.getHolidayCalendar().getHolidayName());
		return custBrHolidayDto;
	}
}
