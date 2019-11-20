package com.lng.dto.masters.custUserMgmt;

public class UserModuleDto {

	private Integer moduleId;
	private String moduleName;
	private String moduleURL;
	private Integer parentId;
	private String icon;
	private String classes;
	
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getClasses() {
		return classes;
	}

	public void setClasses(String classes) {
		this.classes = classes;
	}
	
	

}
