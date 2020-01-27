package com.lng.dto.masters.customerConfig;

public class CustomerConfigDto {

	private Integer custConfigId;
	
	private Integer custId;
	
	private Integer brId;
	
	private String config;
	
	private Boolean statusFlag;

	public Integer getCustConfigId() {
		return custConfigId;
	}

	public void setCustConfigId(Integer custConfigId) {
		this.custConfigId = custConfigId;
	}

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

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public Boolean getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(Boolean statusFlag) {
		this.statusFlag = statusFlag;
	}
}
