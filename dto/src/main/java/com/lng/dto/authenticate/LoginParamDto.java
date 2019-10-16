package com.lng.dto.authenticate;


/**
 * @author Sachin Kulkarni
 * @created 27-sept-2019
 * @description Login request dto
 *
 */
public class LoginParamDto {
	private String loginName;
	private String loginPassword;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	public String getLoginPassword() {
		return loginPassword;
	}
	
	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}
}
