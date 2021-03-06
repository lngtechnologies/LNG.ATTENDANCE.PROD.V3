package com.lng.dto.notification;

import java.util.Date;
import java.util.List;

public class NotificationDto {

	private Integer notificationId;
	
	private String notificationType;
	
	private Integer notificationSentBy;
	
	private Date notificationSentOn;
	
	private String notificationHeader;
	
	private String notificationMessage;
	
	private List<BranchDto> branchDtoList;

	public Integer getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Integer notificationId) {
		this.notificationId = notificationId;
	}

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

	public Date getNotificationSentOn() {
		return notificationSentOn;
	}

	public void setNotificationSentOn(Date notificationSentOn) {
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

	public List<BranchDto> getBranchDtoList() {
		return branchDtoList;
	}

	public void setBranchDtoList(List<BranchDto> branchDtoList) {
		this.branchDtoList = branchDtoList;
	}

}
