package com.lng.dto.masters.custUserMgmt;

import status.Status;

public class CustUserResponseDto {

	private Integer loginId;
	
	public Status status;

	public Integer getLoginId() {
		return loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}
}
