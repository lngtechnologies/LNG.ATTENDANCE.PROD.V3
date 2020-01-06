package com.lng.attendancecustomerservice.service.notification;

import com.lng.dto.notification.DeptNotificationDto;
import com.lng.dto.notification.NotificationDto;
import com.lng.dto.notification.PushNotificationDto;

import status.Status;

public interface PushNotificationService {

	Status saveEmpToken(PushNotificationDto pustNotificationDto);
	
	Status sendPustNotificationToBranch(NotificationDto notificationDto);
	
	Status sendPustNotificationToDepartment(DeptNotificationDto deptNotificationDto);
}
