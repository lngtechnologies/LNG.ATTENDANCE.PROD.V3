package com.lng.attendancecustomerservice.controllers.masters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.masters.CustUserMgmtService;
import com.lng.dto.masters.custUserMgmt.CustUserMgmtDto;

import status.Status;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master/cust/user")
public class CustUserMgmtController {

	@Autowired
	CustUserMgmtService custUserMgmtService;
	
	@PostMapping(value = "/create")
	public ResponseEntity<Status> save(@RequestBody CustUserMgmtDto custUserMgmtDto) {
		Status status = custUserMgmtService.save(custUserMgmtDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}
