package com.lng.dto.notification.company;

import java.util.Date;
import java.util.List;

public class SMSNotificationDto {

	private Integer notificationId;
	
	private String notificationType;
	
	private Integer notificationSentBy;
	
	private Date notificationSentOn;
	
	private String notificationHeader;
	
	private String notificationMessage;
	
	private List<CustNotificationDto> custDto;

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

	public List<CustNotificationDto> getCustDto() {
		return custDto;
	}

	public void setCustDto(List<CustNotificationDto> custDto) {
		this.custDto = custDto;
	}
}
