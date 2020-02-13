package com.lng.attendancetabservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancetabservice.service.CustomerConfigService;
import com.lng.dto.masters.customerConfig.CustomerConfigDto;
import com.lng.dto.masters.customerConfig.CustomerConfigResponse;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/config")
public class CustomerConfigController {
	
	@Autowired
	CustomerConfigService customerConfigService;
	@PostMapping(value = "/getConfigDetailsByCustIdAndBrId")
	public ResponseEntity<CustomerConfigResponse> getConfigDetails(@RequestBody CustomerConfigDto customerConfigDto) {
		CustomerConfigResponse customerConfigResponse = customerConfigService.getConfigDetails(customerConfigDto.getCustId(), customerConfigDto.getBrId());
		if(customerConfigResponse !=null){
			return new ResponseEntity<CustomerConfigResponse>(customerConfigResponse, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

}
