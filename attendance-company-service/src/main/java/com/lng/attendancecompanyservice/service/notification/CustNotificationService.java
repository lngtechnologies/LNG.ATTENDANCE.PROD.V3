package com.lng.attendancecompanyservice.service.notification;

import com.lng.dto.notification.company.CustNotificationDto;
import com.lng.dto.notification.company.CustSMSResponseDto;
import com.lng.dto.notification.company.SMSNotificationDto;

import status.Status;

public interface CustNotificationService {

	CustSMSResponseDto getAllActiveCustomers();
	
	CustSMSResponseDto getAllInActiveCustomers();
	
	Status sendSMSNotificationToAllCustomers(SMSNotificationDto smsNotificationDto);
}
