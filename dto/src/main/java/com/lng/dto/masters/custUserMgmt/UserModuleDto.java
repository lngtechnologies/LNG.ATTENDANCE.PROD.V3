package com.lng.dto.masters.custUserMgmt;

public class UserModuleDto {

	private Integer moduleId;
	private String moduleName;
	private String moduleURL;
	private Integer parentId;
	private Integer userRightId;
	
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
	
	public String getModuleURL() {
		return moduleURL;
	}
	
	public void setModuleURL(String moduleURL) {
		this.moduleURL = moduleURL;
	}
	
	public Integer getParentId() {
		return parentId;
	}
	
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getUserRightId() {
		return userRightId;
	}

	public void setUserRightId(Integer userRightId) {
		this.userRightId = userRightId;
	}
	
}
