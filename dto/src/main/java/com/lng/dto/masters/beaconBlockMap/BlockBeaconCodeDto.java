package com.lng.dto.masters.beaconBlockMap;

public class BlockBeaconCodeDto {
	
	private Integer blkBeaconMapId;

	private Integer refBlkId;
	
	private String beaconCode;
	
	private Integer beaconType;

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

	public Integer getBeaconType() {
		return beaconType;
	}

	public void setBeaconType(Integer beaconType) {
		this.beaconType = beaconType;
	}

	public Integer getBlkBeaconMapId() {
		return blkBeaconMapId;
	}

	public void setBlkBeaconMapId(Integer blkBeaconMapId) {
		this.blkBeaconMapId = blkBeaconMapId;
	}

}
