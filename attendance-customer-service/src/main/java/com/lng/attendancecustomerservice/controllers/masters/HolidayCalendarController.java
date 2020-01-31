package com.lng.attendancecustomerservice.controllers.masters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.masters.HolidayCalendarService;
import com.lng.dto.masters.holidayCalendar.HolidayCalendarDto;
import com.lng.dto.masters.holidayCalendar.HolidayCalendarResponse;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master/holidayCalendar")
public class HolidayCalendarController {
	
	@Autowired
	HolidayCalendarService holidayCalendarService;
	
	@PostMapping(value = "/getAllbyCustId")
	public ResponseEntity<HolidayCalendarResponse> findByCustId(@RequestBody HolidayCalendarDto holidayCalendarDto) {
		HolidayCalendarResponse holidayCalendarResponse = holidayCalendarService.getAllByRefCustId(holidayCalendarDto.getRefCustId());
		return new ResponseEntity<HolidayCalendarResponse>(holidayCalendarResponse, HttpStatus.OK);
	}
	
	@PostMapping(value = "/create")
	public ResponseEntity<HolidayCalendarResponse> save(@RequestBody HolidayCalendarDto holidayCalendarDto) {
		HolidayCalendarResponse response = holidayCalendarService.save(holidayCalendarDto);
		if (holidayCalendarDto !=null){
			return new ResponseEntity<HolidayCalendarResponse>(response, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value="/updateByHolidayId")
	public ResponseEntity<status.Status> update(@RequestBody HolidayCalendarDto holidayCalendarDto){
		status.Status status = holidayCalendarService.updateHolidayCalendarByHolidayId(holidayCalendarDto);
		if(holidayCalendarDto != null){
			return new ResponseEntity<status.Status>(status, HttpStatus.OK);
		}
		return new ResponseEntity<status.Status>(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/deleteByHolidayId")
	public ResponseEntity<HolidayCalendarResponse> delete(@RequestBody HolidayCalendarDto holidayCalendarDto) {
		HolidayCalendarResponse holidayCalendarResponse = holidayCalendarService.deleteByHolidayId(holidayCalendarDto.getHolidayId());
		if(holidayCalendarDto!=null){
			return new ResponseEntity<HolidayCalendarResponse>(holidayCalendarResponse,HttpStatus.OK);
		} return new ResponseEntity(HttpStatus.NO_CONTENT); 
	}

	@PostMapping(value = "/getHolidayCalendarByHolidayId")
	public ResponseEntity<HolidayCalendarResponse> findByHolidayId(@RequestBody HolidayCalendarDto holidayCalendarDto) {
		HolidayCalendarResponse holidayCalendarResponse = holidayCalendarService.getHolidayCalendarByHolidayId(holidayCalendarDto.getHolidayId());
		if (holidayCalendarResponse !=null){
			return new ResponseEntity<HolidayCalendarResponse>(holidayCalendarResponse, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
	
	@GetMapping(value = "/getAll")
	public ResponseEntity<HolidayCalendarResponse> getAll() {
		HolidayCalendarResponse holidayCalendarResponse= holidayCalendarService.getAll();
		if(holidayCalendarResponse.getData1().isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<HolidayCalendarResponse>(holidayCalendarResponse, HttpStatus.OK);
	}
	@PostMapping(value = "/getBranchByCustId")
	public ResponseEntity<HolidayCalendarResponse> findBranchByCustId(@RequestBody HolidayCalendarDto holidayCalendarDto) {
		HolidayCalendarResponse holidayCalendarResponse = holidayCalendarService.findBranchList(holidayCalendarDto.getRefCustId());
		if (holidayCalendarResponse !=null){
			return new ResponseEntity<HolidayCalendarResponse>(holidayCalendarResponse, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping(value = "/getBranchDetailsByCustId")
	public ResponseEntity<HolidayCalendarResponse> findBranchDetailsByCustId(@RequestBody HolidayCalendarDto holidayCalendarDto) {
		HolidayCalendarResponse holidayCalendarResponse = holidayCalendarService.findBranchListByCustId(holidayCalendarDto.getRefCustId());
		if (holidayCalendarResponse !=null){
			return new ResponseEntity<HolidayCalendarResponse>(holidayCalendarResponse, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping(value = "/getHolidayCalendarByCustId")
	public ResponseEntity<HolidayCalendarResponse> findHolidayCalendarByCustId(@RequestBody HolidayCalendarDto holidayCalendarDto) {
		HolidayCalendarResponse holidayCalendarResponse = holidayCalendarService.getHolidayCalendarByRefCustId(holidayCalendarDto.getRefCustId());
		if (holidayCalendarResponse !=null){
			return new ResponseEntity<HolidayCalendarResponse>(holidayCalendarResponse, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
	@PostMapping(value = "/getRemaingHolidayByCustIdAndBrId")
	public ResponseEntity<HolidayCalendarResponse> getRemaingHoliday(@RequestBody HolidayCalendarDto holidayCalendarDto) {
		HolidayCalendarResponse holidayCalendarResponse = holidayCalendarService.getRemaingHoliday(holidayCalendarDto.getRefCustId(),holidayCalendarDto.getRefbrId());
		if(holidayCalendarDto!=null){
			return new ResponseEntity<HolidayCalendarResponse>(holidayCalendarResponse,HttpStatus.OK);
		} return new ResponseEntity(HttpStatus.NO_CONTENT); 
	}
	
	@PostMapping(value = "/getByBrId") 
	public ResponseEntity<HolidayCalendarResponse> findByBrId(@RequestBody HolidayCalendarDto holidayCalendarDto) {
		HolidayCalendarResponse holidayCalendarResponse = holidayCalendarService.getHolidayCalendarByRefBrId(holidayCalendarDto.getRefbrId());
		if (holidayCalendarResponse !=null){
			return new ResponseEntity<HolidayCalendarResponse>(holidayCalendarResponse, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
}
