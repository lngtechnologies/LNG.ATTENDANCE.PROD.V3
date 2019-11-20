package com.lng.dto.masters.custUserMgmt;

import java.util.List;

public class CustUserModuleMapDto {

	private Integer loginId;
	
	private List<CustUserModuleDto> moduleIds;

	public Integer getLoginId() {
		return loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}

	public List<CustUserModuleDto> getModuleIds() {
		return moduleIds;
	}

	public void setModuleIds(List<CustUserModuleDto> moduleIds) {
		this.moduleIds = moduleIds;
	}

}
