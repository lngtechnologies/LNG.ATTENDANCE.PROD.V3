package com.lng.dto.authenticate;


/**
 * @author Sachin Kulkarni
 * @created 27-sept-2019
 * @description Login response dto
 */
public class LoginDto {

	private Integer loginId;	
	private String loginName;
	private String token;
	
	public LoginDto(Integer id, String name, String token) {
		this.loginId = id;
		this.loginName = name;
		this.token = token;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public Integer getLoginId() {
		return loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}

}
