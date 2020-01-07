package com.lng.attendancecustomerservice.controllers.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.notification.PushNotificationService;
import com.lng.dto.notification.DeptNotificationDto;
import com.lng.dto.notification.NotificationDto;
import com.lng.dto.notification.PushNotificationDto;

import status.Status;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/push/notification")
public class PushNotificationController {

	@Autowired
	PushNotificationService pushNotificationService;
	
	@PostMapping(value = "/branchList/send")
	public ResponseEntity<Status> sendToBranch(@RequestBody NotificationDto notificationDto) {
		Status status = pushNotificationService.sendPustNotificationToBranch(notificationDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/departmentList/send")
	public ResponseEntity<Status> sendToDept(@RequestBody DeptNotificationDto deptNotificationDto) {
		Status status = pushNotificationService.sendPustNotificationToDepartment(deptNotificationDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/token/save")
	public ResponseEntity<Status> saveToken(@RequestBody PushNotificationDto pustNotificationDto) {
		Status status = pushNotificationService.saveEmpToken(pustNotificationDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}
