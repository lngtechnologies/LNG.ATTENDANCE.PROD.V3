package com.lng.attendancecustomerservice.service.empAppSetup;

import com.lng.dto.empAttendance.BranchPushNotificationResponse;

public interface BranchPushNotificationService {
	
	BranchPushNotificationResponse sendPustNotificationToBranch(Integer brId,Integer custId);

}
