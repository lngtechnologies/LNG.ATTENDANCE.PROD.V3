package com.lng.dto.customer;

public class MailBounceDto {

	private Integer id;
	
	private String email;
	
	private String smtpId;
	
	private String event;
	
	private String category;
	
	private String sgEventId;
	
	private String sgMessageId;
	
	private String reason;
	
	private String status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSmtpId() {
		return smtpId;
	}

	public void setSmtpId(String smtpId) {
		this.smtpId = smtpId;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSgEventId() {
		return sgEventId;
	}

	public void setSgEventId(String sgEventId) {
		this.sgEventId = sgEventId;
	}

	public String getSgMessageId() {
		return sgMessageId;
	}

	public void setSgMessageId(String sgMessageId) {
		this.sgMessageId = sgMessageId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
