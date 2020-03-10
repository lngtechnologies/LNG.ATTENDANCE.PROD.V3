package com.lng.attendancecustomerservice.serviceImpl.empAppSetup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.Branch;
import com.lng.attendancecustomerservice.repositories.masters.BranchRepository;
import com.lng.attendancecustomerservice.service.empAppSetup.BranchPushNotificationService;
import com.lng.dto.empAttendance.BranchNotificationDto;
import com.lng.dto.empAttendance.BranchPushNotificationResponse;
import com.lng.dto.notification.NotificationDto;

import status.Status;
@Service
public class BranchPushNotificationServiceImpl implements BranchPushNotificationService {

	@Autowired
	BranchRepository branchRepository;


	@Override
	public BranchPushNotificationResponse sendPustNotificationToBranch(Integer brId,Integer custId) {
		
		BranchPushNotificationResponse branchPushNotificationResponse = new BranchPushNotificationResponse();

		List<BranchNotificationDto> pushNotificationList = new ArrayList<BranchNotificationDto>();
		try {
			Branch branch = branchRepository.getByBranchIdAndCustId(brId,custId);
			if(branch != null) {
				List<Object[]>	notificationList = branchRepository.getPushNotificationByBrId(brId);
				if(!notificationList.isEmpty()) {
					for(Object [] n : notificationList) {
						BranchNotificationDto notification =  new BranchNotificationDto();
						notification.setNotificationType(n[0].toString());
						notification.setNotificationSentBy(Integer.valueOf(n[1].toString()));
						notification.setNotificationSentOn(n[2].toString());
						notification.setNotificationHeader(n[3].toString());
						notification.setNotificationMessage(n[4].toString());
						pushNotificationList.add(notification);
						
						branchPushNotificationResponse.setPushNotificationDetails(pushNotificationList);
						branchPushNotificationResponse.status = new Status(false, 200, "Success");
					} 
				}else {
					branchPushNotificationResponse.status = new Status(true, 400, "Not found");
				}

			} else {
				branchPushNotificationResponse.status = new Status(true, 400, "Branch not found");
			}

		} catch (Exception e) {
			branchPushNotificationResponse.status = new Status(true, 500, "Oops..! Something went wrong..");
		}

		return branchPushNotificationResponse;
	}

}
