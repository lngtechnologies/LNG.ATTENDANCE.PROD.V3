package com.lng.dto.masters.beacon;

import java.util.List;

import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapDto;

import status.Status;

public class BlockBeaconMapResponse {
	
	private List<BlockBeaconMapDto> usedBeacons;
	
	private List<BeaconDto> availableBeacons;
	
	public Status status;


	public List<BlockBeaconMapDto> getUsedBeacons() {
		return usedBeacons;
	}

	public void setUsedBeacons(List<BlockBeaconMapDto> usedBeacons) {
		this.usedBeacons = usedBeacons;
	}

	public List<BeaconDto> getAvailableBeacons() {
		return availableBeacons;
	}

	public void setAvailableBeacons(List<BeaconDto> availableBeacons) {
		this.availableBeacons = availableBeacons;
	}
	
}
