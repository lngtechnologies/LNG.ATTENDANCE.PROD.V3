package com.lng.dto.notification;

import java.util.Date;

public class PushNotificationDto {

	private Integer empTokenId;
	
	private Integer empId;
	
	private String token;
	
	private Date createdDate;
	
	private Boolean isActive;

	public Integer getEmpTokenId() {
		return empTokenId;
	}

	public void setEmpTokenId(Integer empTokenId) {
		this.empTokenId = empTokenId;
	}

	public Integer getEmpId() {
		return empId;
	}

	public void setEmpId(Integer empId) {
		this.empId = empId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	
}
