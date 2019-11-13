package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.CustBrHoliday;
import com.lng.attendancecustomerservice.entity.masters.HolidayCalendar;
import com.lng.attendancecustomerservice.repositories.masters.CustBrHolidayRepository;
import com.lng.attendancecustomerservice.repositories.masters.HolidayCalendarRepository;
import com.lng.attendancecustomerservice.service.masters.HolidayCalendarService;
import com.lng.dto.masters.holidayCalendar.HolidayCalendarDto;
import com.lng.dto.masters.holidayCalendar.HolidayCalendarResponse;

import status.Status;
@Service
public class HolidayCalendarServiceImpl implements HolidayCalendarService {

	ModelMapper modelMapper=new ModelMapper();
	@Autowired
	HolidayCalendarRepository holidayCalendarRepository;
	@Autowired
	CustBrHolidayRepository custBrHolidayRepository;

	@Override
	public HolidayCalendarResponse getAllByRefCustId(Integer refCustId) {

		HolidayCalendarResponse  holidayCalendarResponse  =  new  HolidayCalendarResponse();

		try {
			List<HolidayCalendar> holidayCalendars = holidayCalendarRepository.findAllByRefCustId(refCustId);
			holidayCalendarResponse.setData1(holidayCalendars.stream().map(holidayCalendar -> convertToHolidayCalendarDto(holidayCalendar)).collect(Collectors.toList()));
			if(holidayCalendarResponse.getData1().isEmpty()) {
				holidayCalendarResponse.status = new Status(true,400, "Holiday Calendar Not Found");
			}else {
				holidayCalendarResponse.status = new Status(false,200, "success");

			}
		}catch(Exception e) {
			holidayCalendarResponse.status = new Status(true,500, "Something went wrong"); 

		}
		return holidayCalendarResponse;
	}

	@Override
	public HolidayCalendarResponse save(HolidayCalendarDto holidayCalendarDto) {
		HolidayCalendarResponse  holidayCalendarResponse  =  new  HolidayCalendarResponse();

		try{
			if(holidayCalendarDto.getHolidayName() == null || holidayCalendarDto.getHolidayName().isEmpty()) throw new Exception("Please enter Holiday name");

			int a = holidayCalendarRepository.findByRefCustIdAndHolidayName(holidayCalendarDto.getRefCustId(), holidayCalendarDto.getHolidayName());
			if(a == 0) {
				HolidayCalendar holidayCalendar = new HolidayCalendar();
				holidayCalendar.setHolidayCalendarYear(holidayCalendarDto.getHolidayCalendarYear());
				holidayCalendar.setHolidayDate(holidayCalendarDto.getHolidayDate());
				holidayCalendar.setRefCustId(holidayCalendarDto.getRefCustId());
				holidayCalendar.setHolidayName(holidayCalendarDto.getHolidayName());
				holidayCalendarRepository.save(holidayCalendar);
				holidayCalendarResponse.status = new Status(false,200, "Successfully created");

			}
			else{ 
				holidayCalendarResponse.status = new Status(true,400,"Holiday name already exists");
			}

		}catch(Exception ex){
			holidayCalendarResponse.status = new Status(true, 4000, ex.getMessage());
		}

		return holidayCalendarResponse;
	}
	@Override
	public Status updateHolidayCalendarByHolidayId(HolidayCalendarDto holidayCalendarDto) {
		Status status = null;
		try {
			if(holidayCalendarDto.getHolidayName() == null || holidayCalendarDto.getHolidayName().isEmpty()) throw new Exception("Please enter Holiday name");
			if(holidayCalendarDto.getHolidayId() == null || holidayCalendarDto.getHolidayId() == 0) throw new Exception("Holiday id is null or zero");
			if(holidayCalendarDto.getRefCustId() == null || holidayCalendarDto.getRefCustId() == 0) throw new Exception("RefCustId id is null or zero");

			HolidayCalendar holidayCalendar = holidayCalendarRepository.findHolidayCalendarByHolidayId(holidayCalendarDto.getHolidayId());
			HolidayCalendar he = holidayCalendarRepository.findHolidayCalendarByHolidayNameAndRefCustId(holidayCalendarDto.getHolidayName(), holidayCalendarDto.getRefCustId());
			if(he == null) {

				holidayCalendar = modelMapper.map(holidayCalendarDto,HolidayCalendar.class);
				holidayCalendarRepository.save(holidayCalendar);
				status = new Status(false, 200, "Updated successfully");
			} else if (he.getHolidayId() == holidayCalendarDto.getHolidayId()) { 

				holidayCalendar = modelMapper.map(holidayCalendarDto,HolidayCalendar.class);
				holidayCalendarRepository.save(holidayCalendar);
				status = new Status(false, 200, "Updated successfully");
			}
			else{ 

				status = new Status(true,400,"Holiday name already exists");

			}
		}
		catch(Exception e) {
			status = new Status(true, 4000, e.getMessage());
		}
		return status;
	}
	@Override
	public HolidayCalendarResponse deleteByHolidayId(Integer holidayId) {
		HolidayCalendarResponse  holidayCalendarResponse  =  new  HolidayCalendarResponse(); 

		HolidayCalendar holidayCalendar = new HolidayCalendar();
		try {
			holidayCalendar = holidayCalendarRepository.findHolidayCalendarByHolidayId(holidayId);
			if(holidayCalendar != null) {

				// Check weather the branch is used in any transaction or no
				List<CustBrHoliday> custBrHoliday = custBrHolidayRepository.findByHolidayCalendar_HolidayId(holidayId);
				if(custBrHoliday.isEmpty()) {
					holidayCalendarRepository.delete(holidayCalendar);
					holidayCalendarResponse.status = new Status(false, 200, "Deleted successfully");
				}else {
					holidayCalendarRepository.save(holidayCalendar);
					holidayCalendarResponse.status = new Status(false, 200, "The record cannot be deleted as it is already used");
				}
			}else {

				holidayCalendarResponse.status = new Status(true, 400, "Holiday  not found");

			}

		}catch(Exception e) {
			holidayCalendarResponse.status = new Status(true,400, e.getMessage());
		}
		return holidayCalendarResponse;
	}
	/*	try {
			HolidayCalendar holidayCalendar = holidayCalendarRepository.findHolidayCalendarByHolidayId(holidayId);
			if(holidayCalendar != null) {
				holidayCalendarRepository.delete(holidayCalendar);
				holidayCalendarResponse.status = new Status(false,200, "successfully deleted");
			}
			else {
				holidayCalendarResponse.status = new Status(true,400, "Holiday Not Found");
			}

		}catch(Exception e) { 
			holidayCalendarResponse.status = new Status(true,400, e.getMessage());
		}

		return holidayCalendarResponse;
	}*/
	@Override
	public HolidayCalendarResponse getHolidayCalendarByHolidayId(Integer holidayId) {
		HolidayCalendarResponse  holidayCalendarResponse  =  new  HolidayCalendarResponse();
		try {
			HolidayCalendar holidayCalendar = holidayCalendarRepository.findHolidayCalendarByHolidayId(holidayId);
			if(holidayCalendar != null) {
				HolidayCalendarDto holidayCalendarDto = convertToHolidayCalendarDto(holidayCalendar);
				holidayCalendarResponse.data = holidayCalendarDto;
				holidayCalendarResponse.status = new Status(false,200, "Success");
			}
			else {
				holidayCalendarResponse.status = new Status(true, 4000, "Not found");
			}
		}catch(Exception e) {
			holidayCalendarResponse.status = new Status(true,3000, e.getMessage()); 

		}
		return holidayCalendarResponse;
	}

	@Override
	public HolidayCalendarResponse getAll() {
		HolidayCalendarResponse  holidayCalendarResponse  =  new  HolidayCalendarResponse();
		try {
			List<HolidayCalendar> holidayCalendarList=holidayCalendarRepository.findAll();

			holidayCalendarResponse.setData1(holidayCalendarList.stream().map(holidayCalendar -> convertToHolidayCalendarDto(holidayCalendar)).collect(Collectors.toList()));
			if(holidayCalendarResponse.getData1().isEmpty()) {
				holidayCalendarResponse.status = new Status(true,400, "Not found");

			}else {
				holidayCalendarResponse.status = new Status(false,200, "Success");
			}

		}catch(Exception e) {
			holidayCalendarResponse.status = new Status(true,500, e.getMessage()); 

		}
		return holidayCalendarResponse;
	}


	public HolidayCalendarDto convertToHolidayCalendarDto(HolidayCalendar holidayCalendar) {
		HolidayCalendarDto holidayCalendarDto = modelMapper.map(holidayCalendar,HolidayCalendarDto.class);
		//holidayCalendarDto.setRefCustId(holidayCalendar.getCustomer().getCustId());
		//holidayCalendarDto.setCustName(holidayCalendar.getCustomer().getCustName());
		return holidayCalendarDto;
	}


}
