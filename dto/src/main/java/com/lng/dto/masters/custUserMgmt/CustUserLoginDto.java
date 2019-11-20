package com.lng.dto.masters.custUserMgmt;

import java.util.List;

public class CustUserLoginDto {

	private Integer loginId;

	private String loginName;

	private String loginMobile;
	
	private Integer custId;

	private String custName;

	private String custCode;

	private List<CustUserModulesDto> modules;

	private List<CustUserBranchesDto> branches;

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

	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public List<CustUserModulesDto> getModules() {
		return modules;
	}

	public void setModules(List<CustUserModulesDto> modules) {
		this.modules = modules;
	}

	public List<CustUserBranchesDto> getBranches() {
		return branches;
	}

	public void setBranches(List<CustUserBranchesDto> branches) {
		this.branches = branches;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

}
