package com.lng.attendancecustomerservice.service.notification;

import com.lng.dto.notification.NotificationDto;
import com.lng.dto.notification.BranchSmsResponseDto;
import com.lng.dto.notification.DeptNotificationDto;
import com.lng.dto.notification.DeptSmsResponseDto;

import status.Status;

public interface NotificationService {

	BranchSmsResponseDto getBranchListByCustId(Integer custId);
	
	Status sendNotificationToBranchBySms(NotificationDto notificationDto);
	
	DeptSmsResponseDto getDepartmentListByCustId(Integer custId);
	
	Status sendNotificationToDeptBySms(DeptNotificationDto deptNotificationDto);
}
