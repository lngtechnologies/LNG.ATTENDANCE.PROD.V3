package com.lng.dto.masters.customerConfig;

import java.util.List;

import status.Status;

public class DashboardCustConfigResponse {

	private List<DashboardCustConfigDto> configList;
	
	public Status status;

	public List<DashboardCustConfigDto> getConfigList() {
		return configList;
	}

	public void setConfigList(List<DashboardCustConfigDto> configList) {
		this.configList = configList;
	}
}
