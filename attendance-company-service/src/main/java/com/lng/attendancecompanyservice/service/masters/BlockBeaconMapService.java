package com.lng.attendancecompanyservice.service.masters;

import com.lng.dto.blockBeaconMap.BlockBeaconMapDto;
import com.lng.dto.blockBeaconMap.BlockBeaconMapListResponse;

import status.StatusDto;

public interface BlockBeaconMapService {
	
	StatusDto saveBlkBeaconMap(BlockBeaconMapDto blockBeaconMapDto);
	
	BlockBeaconMapListResponse findAll();
	
	StatusDto update(BlockBeaconMapDto blockBeaconMapDto);

}
