package com.lng.dto.masters.customerConfig;

import java.util.List;

import status.Status;

public class CustomerConfigResponse {

	private Integer custId;

	private Integer brId;

	private List<CustomerConfigParamDto> configList;

	public Status status;


	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}

	public Integer getBrId() {
		return brId;
	}

	public void setBrId(Integer brId) {
		this.brId = brId;
	}

	public List<CustomerConfigParamDto> getConfigList() {
		return configList;
	}

	public void setConfigList(List<CustomerConfigParamDto> configList) {
		this.configList = configList;
	}





}
