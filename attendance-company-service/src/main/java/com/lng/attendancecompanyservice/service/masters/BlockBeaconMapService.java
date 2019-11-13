package com.lng.attendancecompanyservice.service.masters;

import java.util.List;

import com.lng.dto.masters.beacon.BlockBeaconMapResponse;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapDto;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapList;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapListResponse;

import status.StatusDto;

public interface BlockBeaconMapService {
	
	StatusDto saveBlkBeaconMap(BlockBeaconMapList blockBeaconMapList);
	
	BlockBeaconMapListResponse findAll();
	
	StatusDto update(BlockBeaconMapList blockBeaconMapList);
	
	BlockBeaconMapListResponse findByCustId(Integer custId);
	
	BlockBeaconMapResponse mapBeacons(BlockBeaconMapDto blockBeaconMapDto);
	
	StatusDto deleteBlockBeaconmap(BlockBeaconMapDto blockBeaconMapDto);

}
