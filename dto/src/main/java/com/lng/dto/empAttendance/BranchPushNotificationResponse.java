package com.lng.dto.empAttendance;

import java.util.List;

import status.Status;

public class BranchPushNotificationResponse {
	
	
	
	public List<BranchNotificationDto> pushNotificationDetails;
	
	public Status status;

	public List<BranchNotificationDto> getPushNotificationDetails() {
		return pushNotificationDetails;
	}

	public void setPushNotificationDetails(List<BranchNotificationDto> pushNotificationDetails) {
		this.pushNotificationDetails = pushNotificationDetails;
	} 


}
