package com.lng.attendancecompanyservice.controllers.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecompanyservice.service.notification.CustNotificationService;
import com.lng.dto.notification.NotificationDto;
import com.lng.dto.notification.company.CustSMSResponseDto;
import com.lng.dto.notification.company.SMSNotificationDto;

import status.Status;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/notification")
public class CustNotificationController {

	@Autowired
	CustNotificationService custNotificationService;
	
	@GetMapping(value = "/getAll/Active/customers")
	public ResponseEntity<CustSMSResponseDto> getAllActive() {
		CustSMSResponseDto custSMSResponseDto= custNotificationService.getAllActiveCustomers();
		return new ResponseEntity<CustSMSResponseDto>(custSMSResponseDto, HttpStatus.OK);
	}
	
	@GetMapping(value = "/getAll/InActive/customers")
	public ResponseEntity<CustSMSResponseDto> getAllInActive() {
		CustSMSResponseDto custSMSResponseDto= custNotificationService.getAllInActiveCustomers();
		return new ResponseEntity<CustSMSResponseDto>(custSMSResponseDto, HttpStatus.OK);
	}
	
	@PostMapping(value = "/customerList/send/sms")
	public ResponseEntity<Status> brSms(@RequestBody SMSNotificationDto smsNotificationDto) {
		Status status = custNotificationService.sendSMSNotificationToAllCustomers(smsNotificationDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}
