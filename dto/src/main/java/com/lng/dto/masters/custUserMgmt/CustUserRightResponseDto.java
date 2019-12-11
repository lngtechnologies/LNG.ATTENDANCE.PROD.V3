package com.lng.dto.masters.custUserMgmt;

import java.util.List;

import status.Status;

public class CustUserRightResponseDto {

	public Status status;
	
	private List<UserModuleDto> assignedModules;
	
	private List<UserModuleDto> unAssignedModules;

	private List<UserModuleDto> allModules;
	
	public List<UserModuleDto> getAssignedModules() {
		return assignedModules;
	}

	public void setAssignedModules(List<UserModuleDto> assignedModules) {
		this.assignedModules = assignedModules;
	}

	public List<UserModuleDto> getUnAssignedModules() {
		return unAssignedModules;
	}

	public void setUnAssignedModules(List<UserModuleDto> unAssignedModules) {
		this.unAssignedModules = unAssignedModules;
	}

	public List<UserModuleDto> getAllModules() {
		return allModules;
	}

	public void setAllModules(List<UserModuleDto> allModules) {
		this.allModules = allModules;
	}
	
	
}
