package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.ArrayList;
import java.util.Date;
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
				holidayCalendarResponse.status = new Status(false,400, "Not Found");
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
			int b = holidayCalendarRepository.findByRefCustIdAndHolidayDate(holidayCalendarDto.getRefCustId(), holidayCalendarDto.getHolidayDate());
			if(b == 0) {
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
			}
			else{ 
				holidayCalendarResponse.status = new Status(true,400,"Holiday Date already exists");
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
			HolidayCalendar h = holidayCalendarRepository.findHolidayCalendarByHolidayDateAndRefCustId(holidayCalendarDto.getHolidayDate(), holidayCalendarDto.getRefCustId());
			HolidayCalendar he = holidayCalendarRepository.findHolidayCalendarByHolidayNameAndRefCustId(holidayCalendarDto.getHolidayName(), holidayCalendarDto.getRefCustId());
			if(h == null ) {
				if(he == null  ) {

					holidayCalendar = modelMapper.map(holidayCalendarDto,HolidayCalendar.class);
					holidayCalendarRepository.save(holidayCalendar);
					status = new Status(false, 200, "Updated successfully");

				} else if ( he.getHolidayId() == holidayCalendarDto.getHolidayId()) { 

					holidayCalendar = modelMapper.map(holidayCalendarDto,HolidayCalendar.class);
					holidayCalendarRepository.save(holidayCalendar);
					status = new Status(false, 200, "Updated successfully");
				}
			}
			else if(h != null && h.getHolidayId() == holidayCalendarDto.getHolidayId() ) {
				HolidayCalendar he1 = holidayCalendarRepository.findHolidayCalendarByHolidayNameAndRefCustId(holidayCalendarDto.getHolidayName(), holidayCalendarDto.getRefCustId());
				if(he1 == null) {

					holidayCalendar = modelMapper.map(holidayCalendarDto,HolidayCalendar.class);
					holidayCalendarRepository.save(holidayCalendar);
					status = new Status(false, 200, "Updated successfully");
				} else if (he1.getHolidayId() == holidayCalendarDto.getHolidayId()) { 

					holidayCalendar = modelMapper.map(holidayCalendarDto,HolidayCalendar.class);
					holidayCalendarRepository.save(holidayCalendar);
					status = new Status(false, 200, "Updated successfully");
				}
				else{ 

					status = new Status(true,400,"Holiday name already exists");

				}
			}
			else{ 
				status = new Status(true,400,"Holiday Date already exists");
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

				// Check weather the HolidayCalendar is used in any transaction or no
				List<CustBrHoliday> custBrHoliday = custBrHolidayRepository.findByHolidayCalendar_HolidayId(holidayId);
				if(custBrHoliday.isEmpty()) {
					holidayCalendarRepository.delete(holidayCalendar);
					holidayCalendarResponse.status = new Status(false, 200, "Deleted successfully");
				}else {
					holidayCalendarRepository.save(holidayCalendar);
					holidayCalendarResponse.status = new Status(false, 200, "The record cannot be deleted as it is already used");
				}
			}else {

				holidayCalendarResponse.status = new Status(false, 400, "Holiday  not found");

			}

		}catch(Exception e) {
			holidayCalendarResponse.status = new Status(true,4000, e.getMessage());
		}
		return holidayCalendarResponse;
	}

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
				holidayCalendarResponse.status = new Status(false, 400, "Not found");
			}
		}catch(Exception e) {
			holidayCalendarResponse.status = new Status(true,500, e.getMessage()); 

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
				holidayCalendarResponse.status = new Status(false,400, "Not found");

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

	@Override
	public HolidayCalendarResponse findBranchList(Integer refCustId) {
		HolidayCalendarResponse  holidayCalendarResponse  =  new  HolidayCalendarResponse();
		List<HolidayCalendarDto> HolidayCalendarDtoList = new ArrayList<>();
		try {

			List<Object[]> branchList = holidayCalendarRepository.findBranchByRefCustomerId(refCustId);
			if(branchList.isEmpty()) {
				holidayCalendarResponse.status = new Status(false,400, "Customer Not Found");
			}else {
				for (Object[] p : branchList) {	

					HolidayCalendarDto holidayCalendarDto1 = new HolidayCalendarDto();
					holidayCalendarDto1.setRefbrId(Integer.valueOf(p[0].toString()));
					holidayCalendarDto1.setBrName((p[1].toString()));
					HolidayCalendarDtoList.add(holidayCalendarDto1);
					holidayCalendarResponse.status = new Status(false,200, "success");
				}

			}

		}catch (Exception e){
			holidayCalendarResponse.status = new Status(true,500, e.getMessage());


		}
		holidayCalendarResponse.setData1(HolidayCalendarDtoList);
		return holidayCalendarResponse;
	}

	@Override
	public HolidayCalendarResponse getHolidayCalendarByRefCustId(Integer refCustId) {
		HolidayCalendarResponse  holidayCalendarResponse  =  new  HolidayCalendarResponse();
		try {
			List<HolidayCalendar> holidayList = holidayCalendarRepository.findHolidayCalendarByrefCustId(refCustId);
			if(holidayList.isEmpty()) {
				holidayCalendarResponse.status = new Status(false,400, " Not Found");
			}else { 
				holidayCalendarResponse.setData1(holidayList.stream().map(holidayCalendar -> convertToHolidayCalendarDto(holidayCalendar)).collect(Collectors.toList()));
				holidayCalendarResponse.status = new Status(false,200, "success");
			}
		}catch(Exception e) {
			holidayCalendarResponse.status = new Status(true,500, "Something went wrong"); 

		}
		return holidayCalendarResponse;
	}

	@Override
	public HolidayCalendarResponse getRemaingHoliday(Integer refCustId) {
		HolidayCalendarResponse  holidayCalendarResponse  =  new  HolidayCalendarResponse();
		try {

			List<HolidayCalendar> holidayCalendarList = holidayCalendarRepository.findHolidayCalendarByRefCustId(refCustId);
			if(holidayCalendarList.isEmpty()) {
				holidayCalendarResponse.status = new Status(false,400, " Not Found");
			}else {
				holidayCalendarResponse.setData1(holidayCalendarList.stream().map(holidayCalendar -> convertToHolidayCalendarDto(holidayCalendar)).collect(Collectors.toList()));
				holidayCalendarResponse.status = new Status(false,200, "success");
			}

		}catch(Exception ex){
			holidayCalendarResponse.status = new Status(true,500, "Something went wrong"); 
		}

		return holidayCalendarResponse;
	}

	@Override
	public HolidayCalendarResponse getHolidayCalendarByRefBrId(Integer refbrId) {
		HolidayCalendarResponse  holidayCalendarResponse  =  new  HolidayCalendarResponse();
		List<HolidayCalendarDto> HolidayCalendarDtoList = new ArrayList<>();
		try {
			List<Object[]> holidayList =  holidayCalendarRepository.findHolidayCalendarBybrId(refbrId);

			if(holidayList.isEmpty()) {
				holidayCalendarResponse.status = new Status(false,400, " Not Found");
			}else {
				for (Object[] p : holidayList) {	

					HolidayCalendarDto holidayCalendarDto1 = new HolidayCalendarDto();
					holidayCalendarDto1.setHolidayId(Integer.valueOf(p[0].toString()));
					holidayCalendarDto1.setRefCustId(Integer.valueOf(p[1].toString()));
					holidayCalendarDto1.setHolidayCalendarYear((p[2].toString()));
					holidayCalendarDto1.setHolidayDate((Date)p[3]);
					holidayCalendarDto1.setHolidayName((p[4].toString()));
					holidayCalendarDto1.setCustBrHolidayId(Integer.valueOf(p[5].toString()));
					holidayCalendarDto1.setRefbrId(Integer.valueOf(p[6].toString()));
					HolidayCalendarDtoList.add(holidayCalendarDto1);
					holidayCalendarResponse.status = new Status(false,200, "success");
				}

			}

		}catch (Exception e){
			holidayCalendarResponse.status = new Status(true,500, e.getMessage());


		}
		holidayCalendarResponse.setData1(HolidayCalendarDtoList);
		return holidayCalendarResponse;
	}

}


