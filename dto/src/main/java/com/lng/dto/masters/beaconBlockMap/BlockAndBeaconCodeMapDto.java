package com.lng.dto.masters.beaconBlockMap;

import java.util.List;

import status.Status;

public class BlockAndBeaconCodeMapDto {
	
	private List<BlockBeaconMapResponseDto> beaconMapResponseDtoList;
	
	public Status status;

	public List<BlockBeaconMapResponseDto> getBeaconMapResponseDtoList() {
		return beaconMapResponseDtoList;
	}

	public void setBeaconMapResponseDtoList(List<BlockBeaconMapResponseDto> beaconMapResponseDtoList) {
		this.beaconMapResponseDtoList = beaconMapResponseDtoList;
	}
	
	
}
