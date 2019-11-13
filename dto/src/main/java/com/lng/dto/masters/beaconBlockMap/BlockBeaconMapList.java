package com.lng.dto.masters.beaconBlockMap;

import java.util.List;

public class BlockBeaconMapList {

	private Integer blkBeaconMapId;
	
	private Integer refBlkId;
	
	private Integer beaconType;
	
	private List<BlockBeaconMapDto> beacons;

	public Integer getRefBlkId() {
		return refBlkId;
	}

	public void setRefBlkId(Integer refBlkId) {
		this.refBlkId = refBlkId;
	}

	public List<BlockBeaconMapDto> getBeacons() {
		return beacons;
	}

	public void setBeacons(List<BlockBeaconMapDto> beacons) {
		this.beacons = beacons;
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
