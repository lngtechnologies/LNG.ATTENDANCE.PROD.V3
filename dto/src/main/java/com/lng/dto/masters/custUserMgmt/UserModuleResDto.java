package com.lng.dto.masters.custUserMgmt;

import java.util.List;

import status.Status;

public class UserModuleResDto {

	private List<UserModuleResponseDto> modules;
	
	public Status status;

	public List<UserModuleResponseDto> getModules() {
		return modules;
	}

	public void setModules(List<UserModuleResponseDto> modules) {
		this.modules = modules;
	}
	
}
