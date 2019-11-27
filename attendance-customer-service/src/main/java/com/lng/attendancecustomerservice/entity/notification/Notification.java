package com.lng.attendancecustomerservice.entity.notification;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ttnotification")
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notificationId")
	private Integer notificationId;

	@Column(name = "notificationType")
	private String notificationType;

	@Column(name = "notificationSentBy")
	private Integer notificationSentBy;

	@Column(name = "notificationSentOn")
	private Date notificationSentOn;

	@Column(name = "notificationHeader")
	private String notificationHeader;

	@Column(name = "notificationMessage")
	private String notificationMessage;

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

}
