package com.lng.dto.masters.custUserMgmt;

public class CustUserMgmtDto {
	
	private Integer customerId;
	
	private Integer loginId;

	private String userName;
	
	private String uMobileNumber;

	
	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getuMobileNumber() {
		return uMobileNumber;
	}

	public void setuMobileNumber(String uMobileNumber) {
		this.uMobileNumber = uMobileNumber;
	}

	public Integer getLoginId() {
		return loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}

}
