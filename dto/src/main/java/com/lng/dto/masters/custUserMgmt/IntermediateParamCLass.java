package com.lng.dto.masters.custUserMgmt;

import java.util.List;

public class IntermediateParamCLass {
	private String userName;
	private String uMobileNumber;
	public List<CustUserModuleDto> modules;
	
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
}
