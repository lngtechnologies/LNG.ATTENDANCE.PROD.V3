package com.lng.attendancecompanyservice.service.masters;

import com.lng.dto.masters.beacon.BlockBeaconMapResponse;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapDto;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapListResponse;

import status.StatusDto;

public interface BlockBeaconMapService {
	
	StatusDto saveBlkBeaconMap(BlockBeaconMapDto blockBeaconMapDto);
	
	BlockBeaconMapListResponse findAll();
	
	StatusDto update(BlockBeaconMapDto blockBeaconMapDto);
	
	BlockBeaconMapListResponse findByCustId(Integer custId);
	
	BlockBeaconMapResponse mapBeacons(BlockBeaconMapDto blockBeaconMapDto);
	
	StatusDto deleteBlockBeaconmap(BlockBeaconMapDto blockBeaconMapDto);

}
