package com.lng.dto.notification.company;

import java.util.List;

import status.Status;

public class CustSMSResponseDto {

	private List<CustNotificationDto> custDto;
	
	public Status status;

	public List<CustNotificationDto> getCustDto() {
		return custDto;
	}

	public void setCustDto(List<CustNotificationDto> custDto) {
		this.custDto = custDto;
	}
}
