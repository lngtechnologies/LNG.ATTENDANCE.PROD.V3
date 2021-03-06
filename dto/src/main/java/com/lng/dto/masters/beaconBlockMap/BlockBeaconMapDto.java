package com.lng.dto.masters.beaconBlockMap;

import java.util.Date;

public class BlockBeaconMapDto {

	private Integer blkBeaconMapId;
	
	private Integer refBlkId;
	
	private Integer custId;
	
	private Integer brId;
	
	private String beaconCode;
	
	private Integer beaconType;
	
	private Boolean blkBeaconMapIsActive;
	
	private Date blkBeaconMapCreatedDate;
	
	private String blkLogicalName;

	public Integer getBlkBeaconMapId() {
		return blkBeaconMapId;
	}

	public void setBlkBeaconMapId(Integer blkBeaconMapId) {
		this.blkBeaconMapId = blkBeaconMapId;
	}

	public Integer getRefBlkId() {
		return refBlkId;
	}

	public void setRefBlkId(Integer refBlkId) {
		this.refBlkId = refBlkId;
	}

	public String getBeaconCode() {
		return beaconCode;
	}

	public void setBeaconCode(String beaconCode) {
		this.beaconCode = beaconCode;
	}

	public Boolean getBlkBeaconMapIsActive() {
		return blkBeaconMapIsActive;
	}

	public void setBlkBeaconMapIsActive(Boolean blkBeaconMapIsActive) {
		this.blkBeaconMapIsActive = blkBeaconMapIsActive;
	}

	public Date getBlkBeaconMapCreatedDate() {
		return blkBeaconMapCreatedDate;
	}

	public void setBlkBeaconMapCreatedDate(Date blkBeaconMapCreatedDate) {
		this.blkBeaconMapCreatedDate = blkBeaconMapCreatedDate;
	}

	public String getBlkLogicalName() {
		return blkLogicalName;
	}

	public void setBlkLogicalName(String blkLogicalName) {
		this.blkLogicalName = blkLogicalName;
	}

	public Integer getBeaconType() {
		return beaconType;
	}

	public void setBeaconType(Integer beaconType) {
		this.beaconType = beaconType;
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

	
}
