package com.lng.attendancetabservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancetabservice.service.CustomerService;
import com.lng.dto.employeeAppSetup.OtpResponseDto;
import com.lng.dto.tabService.CustomerDto1;
import com.lng.dto.tabService.CustomerResponse1;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping(value="/customer/setup")
public class CustomerController {
	
	@Autowired
	CustomerService customerService;
	
	
	@PostMapping(value = "/generateOtp")
    public ResponseEntity<OtpResponseDto> generateOtp(@RequestBody CustomerDto1 customerDto) {
		OtpResponseDto otpResponseDto = customerService.generateOtp(customerDto.getCustCode(), customerDto.getBrCode());
        if (otpResponseDto !=null){
            return new ResponseEntity<OtpResponseDto>(otpResponseDto, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
	
	
	@PostMapping(value = "/getByBrCodeAndCustCode") 
	public ResponseEntity<CustomerResponse1> findByBrCodeAndCustCode(@RequestBody CustomerDto1 customerDto) {
		CustomerResponse1 customerResponse = customerService.getCustBranchDetails(customerDto.getCustCode(), customerDto.getBrCode());
		if (customerResponse !=null){
			return new ResponseEntity<CustomerResponse1>(customerResponse, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}

}
