package com.lng.attendancecustomerservice.controllers.empAppSetup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.empAppSetup.BranchPushNotificationService;
import com.lng.dto.empAttendance.BranchPushNotificationResponse;
import com.lng.dto.notification.BranchDto;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value="/mobile/pushNotification")
public class BranchPushNotificationController {
	
	@Autowired
	BranchPushNotificationService branchPushNotificationService;

	@PostMapping(value = "/getByBrIdAndCustId")
	public ResponseEntity<BranchPushNotificationResponse> pushNotification(@RequestBody BranchDto branchDto) {
		BranchPushNotificationResponse branchPushNotificationResponse = branchPushNotificationService.sendPustNotificationToBranch(branchDto.getBrId(), branchDto.getCustId());
		if (branchPushNotificationResponse !=null){
			return new ResponseEntity<BranchPushNotificationResponse>(branchPushNotificationResponse, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	

}
