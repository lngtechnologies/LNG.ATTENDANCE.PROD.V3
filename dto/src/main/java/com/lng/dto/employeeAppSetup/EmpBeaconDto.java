package com.lng.dto.employeeAppSetup;

import java.util.List;

import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapDto;

public class EmpBeaconDto {

	private List<BlockBeaconMapDto> beaconMapDtolist;

	public List<BlockBeaconMapDto> getBeaconMapDtolist() {
		return beaconMapDtolist;
	}

	public void setBeaconMapDtolist(List<BlockBeaconMapDto> beaconMapDtolist) {
		this.beaconMapDtolist = beaconMapDtolist;
	}
}
