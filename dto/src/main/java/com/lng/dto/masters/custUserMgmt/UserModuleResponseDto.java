package com.lng.dto.masters.custUserMgmt;

import status.Status;

public class UserModuleResponseDto {

	private Integer moduleId;
	private String moduleName;
	private String moduleURL;
	private Integer parentId;
	
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
}
