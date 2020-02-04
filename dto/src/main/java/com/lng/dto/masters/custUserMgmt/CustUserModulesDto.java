package com.lng.dto.masters.custUserMgmt;

public class CustUserModulesDto {

	private Integer loginId;
	
	private Integer moduleId;
	
	private String moduleName;
	
	private  Integer  userRightId;

	public Integer getLoginId() {
		return loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}

	public Integer getModuleId() {
		return moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public Integer getUserRightId() {
		return userRightId;
	}

	public void setUserRightId(Integer userRightId) {
		this.userRightId = userRightId;
	}

	

}
