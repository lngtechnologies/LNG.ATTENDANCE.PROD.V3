package com.lng.dto.empAttendance;

public class BranchNotificationDto {
	
	private String notificationType;
	
	private Integer notificationSentBy;
	
	private String notificationSentOn;
	
	private String notificationHeader;
	
	private String notificationMessage;


	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public Integer getNotificationSentBy() {
		return notificationSentBy;
	}

	public void setNotificationSentBy(Integer notificationSentBy) {
		this.notificationSentBy = notificationSentBy;
	}

	public String getNotificationSentOn() {
		return notificationSentOn;
	}

	public void setNotificationSentOn(String notificationSentOn) {
		this.notificationSentOn = notificationSentOn;
	}

	public String getNotificationHeader() {
		return notificationHeader;
	}

	public void setNotificationHeader(String notificationHeader) {
		this.notificationHeader = notificationHeader;
	}

	public String getNotificationMessage() {
		return notificationMessage;
	}

	public void setNotificationMessage(String notificationMessage) {
		this.notificationMessage = notificationMessage;
	}
	
	

}
