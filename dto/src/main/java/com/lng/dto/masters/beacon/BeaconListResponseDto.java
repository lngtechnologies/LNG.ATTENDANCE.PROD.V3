package com.lng.dto.masters.beacon;

import java.util.List;

import status.Status;

public class BeaconListResponseDto {

	private List<BeaconDto> beaconDtoList;
	
	public Status status;

	public List<BeaconDto> getBeaconDtoList() {
		return beaconDtoList;
	}

	public void setBeaconDtoList(List<BeaconDto> beaconDtoList) {
		this.beaconDtoList = beaconDtoList;
	}
	
	
}
