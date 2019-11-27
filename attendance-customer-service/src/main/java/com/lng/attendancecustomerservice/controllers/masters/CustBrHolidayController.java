package com.lng.attendancecustomerservice.controllers.masters;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.masters.CustBrHolidayService;
import com.lng.dto.masters.custBrHoliday.CustBrHolidayDto;
import com.lng.dto.masters.custBrHoliday.CustBrHolidayResponse;
@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/cust/brholiday")
public class CustBrHolidayController {
	@Autowired
   CustBrHolidayService custBrHolidayService;
	
	@PostMapping(value = "/SaveBranchCalendarByBranchIdAndCustdAndHolidayName")
	public ResponseEntity<CustBrHolidayResponse> findBranchHolidayCalendar(@RequestBody List<CustBrHolidayDto> custBrHolidayDto) {
		CustBrHolidayResponse custBrHolidayResponse = custBrHolidayService.saveCustBrHoliday(custBrHolidayDto);
		if (custBrHolidayResponse !=null){
			return new ResponseEntity<CustBrHolidayResponse>(custBrHolidayResponse, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
	@PostMapping(value = "/SaveBranchCalendarByBranchIdAndHolidayName")
	public ResponseEntity<CustBrHolidayResponse> save(@RequestBody List<CustBrHolidayDto> custBrHolidayDto) {
		CustBrHolidayResponse custBrHolidayResponse = custBrHolidayService.saveCustBrHoliday(custBrHolidayDto);
		if (custBrHolidayResponse !=null){
			return new ResponseEntity<CustBrHolidayResponse>(custBrHolidayResponse, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
	@PostMapping(value = "/deleteByCustBrHolidayId")
	public ResponseEntity<CustBrHolidayResponse> delete(@RequestBody CustBrHolidayDto custBrHolidayDto) {
		CustBrHolidayResponse custBrHolidayResponse = custBrHolidayService.delete(custBrHolidayDto.getCustBrHolidayId());
		if(custBrHolidayDto!=null){
			return new ResponseEntity<CustBrHolidayResponse>(custBrHolidayResponse,HttpStatus.OK);
		} return new ResponseEntity(HttpStatus.NO_CONTENT); 
	}
	@PostMapping(value="/updateByCustBrHolidayId")
	public ResponseEntity<status.Status> update(@RequestBody CustBrHolidayDto custBrHolidayDto){
		status.Status status = custBrHolidayService.updateCustBrHoliday(custBrHolidayDto);
		if(custBrHolidayDto != null){
			return new ResponseEntity<status.Status>(status, HttpStatus.OK);
		}
		return new ResponseEntity<status.Status>(HttpStatus.NO_CONTENT);
	}
}
