package com.lng.attendancecustomerservice.controllers.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.notification.NotificationService;
import com.lng.dto.notification.BranchDto;
import com.lng.dto.notification.BranchSmsResponseDto;
import com.lng.dto.notification.DepartmentDto;
import com.lng.dto.notification.DeptNotificationDto;
import com.lng.dto.notification.DeptSmsResponseDto;
import com.lng.dto.notification.NotificationDto;

import status.Status;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/notification")
public class NotificationController {

	@Autowired
	NotificationService notificationService;
	
	
	@PostMapping(value = "/branch/getByCustId")
	public ResponseEntity<BranchSmsResponseDto> getBranchListByCustId(@RequestBody BranchDto branchDto) {
		BranchSmsResponseDto branchSmsResponseDto = notificationService.getBranchListByCustId(branchDto.getCustId());
		return new ResponseEntity<BranchSmsResponseDto>(branchSmsResponseDto, HttpStatus.OK);
	}
	
	@PostMapping(value = "/branch/getAllByCustId")
	public ResponseEntity<BranchSmsResponseDto> getAllBranchListByCustId(@RequestBody BranchDto branchDto) {
		BranchSmsResponseDto branchSmsResponseDto = notificationService.getAllBranchestByCustId(branchDto.getCustId());
		return new ResponseEntity<BranchSmsResponseDto>(branchSmsResponseDto, HttpStatus.OK);
	}
	
	@PostMapping(value = "/department/getByCustId")
	public ResponseEntity<DeptSmsResponseDto> getDepartmentListByCustId(@RequestBody DepartmentDto departmentDto) {
		DeptSmsResponseDto deptSmsResponseDto = notificationService.getDepartmentListByCustId(departmentDto.getCustId());
		return new ResponseEntity<DeptSmsResponseDto>(deptSmsResponseDto, HttpStatus.OK);
	}
	
	@PostMapping(value = "/branchList/send/sms")
	public ResponseEntity<Status> brSms(@RequestBody NotificationDto notificationDto) {
		Status status = notificationService.sendNotificationToBranchBySms(notificationDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/departmentList/send/sms")
	public ResponseEntity<Status> save(@RequestBody DeptNotificationDto deptNotificationDto) {
		Status status = notificationService.sendNotificationToDeptBySms(deptNotificationDto);
		if (status !=null){
			return new ResponseEntity<Status>(status, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}
