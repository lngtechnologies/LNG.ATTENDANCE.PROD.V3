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

import com.lng.attendancecustomerservice.service.masters.CustLeaveService;
import com.lng.dto.masters.custLeave.CustLeaveResponse;
import com.lng.dto.masters.custLeave.custLeaveDto;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master/custLeave")
public class CustLeaveController {
	
	@Autowired
	CustLeaveService custLeaveService;
	
	@PostMapping(value = "/create")
	public ResponseEntity<CustLeaveResponse> save(@RequestBody custLeaveDto custLeaveDto) {
		CustLeaveResponse response = custLeaveService.saveCustLeave(custLeaveDto);
		if (custLeaveDto !=null){
			return new ResponseEntity<CustLeaveResponse>(response, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(value = "/getAll")
	public ResponseEntity<CustLeaveResponse> getAll() {
		CustLeaveResponse custLeaveDto= custLeaveService.getAll();
		if(custLeaveDto.getData1().isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<CustLeaveResponse>(custLeaveDto, HttpStatus.OK);
	}

	@PostMapping(value="/updateByCustLeaveId")
	public ResponseEntity<status.Status> update(@RequestBody custLeaveDto custLeaveDto){
		status.Status status = custLeaveService.updateCustLeaveByCustLeaveId(custLeaveDto);
		if(custLeaveDto != null){
			return new ResponseEntity<status.Status>(status, HttpStatus.OK);
		}
		return new ResponseEntity<status.Status>(HttpStatus.NO_CONTENT);
	}
	@PostMapping(value = "/deleteByCustLeaveId")
	public ResponseEntity<CustLeaveResponse> delete(@RequestBody custLeaveDto custLeaveDto) {
		CustLeaveResponse custLeaveDto2 = custLeaveService.deleteByCustLeaveId(custLeaveDto.getCustLeaveId());
		if(custLeaveDto!=null){
			return new ResponseEntity<CustLeaveResponse>(custLeaveDto2,HttpStatus.OK);
		} return new ResponseEntity(HttpStatus.NO_CONTENT); 
	}
	
	@PostMapping(value = "/getCustLeaveByCustLeaveId")
	public ResponseEntity<CustLeaveResponse> findByCustLeaveId(@RequestBody custLeaveDto custLeaveDto) {
		CustLeaveResponse custLeaveResponse = custLeaveService.getCustLeaveByCustLeaveId(custLeaveDto.getCustLeaveId());
		if (custLeaveResponse !=null){
			return new ResponseEntity<CustLeaveResponse>(custLeaveResponse, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping(value = "/getAllByCustId")
	public ResponseEntity<CustLeaveResponse> findByCustId(@RequestBody custLeaveDto custLeaveDto) {
		CustLeaveResponse custLeaveResponse = custLeaveService.getAllByCustId(custLeaveDto.getRefCustId());
		return new ResponseEntity<CustLeaveResponse>(custLeaveResponse, HttpStatus.OK);
	}

}
