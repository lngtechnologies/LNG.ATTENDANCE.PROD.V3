package com.lng.dto.masters.beacon;

import java.util.Date;

public class BeaconDto {

	private Integer beaconId;
	
	private String beaconCode;
	
	private Date beaconCreatedDate;

	public Integer getBeaconId() {
		return beaconId;
	}

	public void setBeaconId(Integer beaconId) {
		this.beaconId = beaconId;
	}

	public String getBeaconCode() {
		return beaconCode;
	}

	public void setBeaconCode(String beaconCode) {
		this.beaconCode = beaconCode;
	}

	public Date getBeaconCreatedDate() {
		return beaconCreatedDate;
	}

	public void setBeaconCreatedDate(Date beaconCreatedDate) {
		this.beaconCreatedDate = beaconCreatedDate;
	}
	
}
