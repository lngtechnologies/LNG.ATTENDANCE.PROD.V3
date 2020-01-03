package com.lng.attendancecustomerservice.service.notification;

import com.lng.dto.notification.PustNotificationDto;

public interface PushNotificationService {

	PustNotificationDto sendPustNotificationDto(PustNotificationDto pustNotificationDto);
}
