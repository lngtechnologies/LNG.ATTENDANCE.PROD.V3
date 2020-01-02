package com.lng.attendancecompanyservice.service.masters;

import com.lng.dto.masters.beacon.BlockBeaconMapResponse;
import com.lng.dto.masters.beaconBlockMap.BlockAndBeaconCodeMapDto;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapDto;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapList;

import status.StatusDto;

public interface BlockBeaconMapService {
	
	StatusDto saveBlkBeaconMap(BlockBeaconMapList blockBeaconMapList);
	
	//BlockBeaconMapListResponse findAll();
	
	BlockAndBeaconCodeMapDto findAll();
	
	StatusDto update(BlockBeaconMapList blockBeaconMapList);
	
	// BlockBeaconMapListResponse findByCustId(Integer custId);
	BlockAndBeaconCodeMapDto findByCustId(Integer custId);
	
	BlockBeaconMapResponse mapBeacons(BlockBeaconMapDto blockBeaconMapDto);
	
	StatusDto deleteBlockBeaconmap(BlockBeaconMapDto blockBeaconMapDto);
	
	BlockAndBeaconCodeMapDto findByCustIdAndBrId(Integer custId,Integer brId );
	

}
