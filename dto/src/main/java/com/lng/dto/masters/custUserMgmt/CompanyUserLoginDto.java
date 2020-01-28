package com.lng.dto.masters.custUserMgmt;

import java.util.List;

public class CompanyUserLoginDto {
	
	private Integer loginId;

	private String loginName;

	private String loginMobile;
	
	private Integer custId;

	private List<CustUserModulesDto> modules;

	public Integer getLoginId() {
		return loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginMobile() {
		return loginMobile;
	}

	public void setLoginMobile(String loginMobile) {
		this.loginMobile = loginMobile;
	}

	public List<CustUserModulesDto> getModules() {
		return modules;
	}

	public void setModules(List<CustUserModulesDto> modules) {
		this.modules = modules;
	}

	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}

}
