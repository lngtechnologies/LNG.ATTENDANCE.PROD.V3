package com.lng.dto.masters.beaconBlockMap;

import java.util.List;

import status.Status;

public class BlockBeaconMapListResponse {

	private List<BlockBeaconMapDto> beaconMapDtolist;
	
	public Status status;

	public List<BlockBeaconMapDto> getBeaconMapDtolist() {
		return beaconMapDtolist;
	}

	public void setBeaconMapDtolist(List<BlockBeaconMapDto> beaconMapDtolist) {
		this.beaconMapDtolist = beaconMapDtolist;
	}

}
