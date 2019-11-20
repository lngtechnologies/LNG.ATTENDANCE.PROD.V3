package com.lng.dto.masters.custUserMgmt;

public class CustUserBranchDto {

	private Integer loginDataRightId;
	
	private Integer brId;
	
	private String brName;

	public Integer getBrId() {
		return brId;
	}

	public void setBrId(Integer brId) {
		this.brId = brId;
	}

	public String getBrName() {
		return brName;
	}

	public void setBrName(String brName) {
		this.brName = brName;
	}

	public Integer getLoginDataRightId() {
		return loginDataRightId;
	}

	public void setLoginDataRightId(Integer loginDataRightId) {
		this.loginDataRightId = loginDataRightId;
	}

}
