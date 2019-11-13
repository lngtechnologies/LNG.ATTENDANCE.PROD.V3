package com.lng.attendancecompanyservice.service.masters;

import com.lng.dto.masters.beacon.BeaconDto;
import com.lng.dto.masters.beacon.BeaconListResponseDto;
import com.lng.dto.masters.beacon.BlockBeaconMapResponse;

import status.Status;
import status.StatusDto;

public interface BeaconService {
	
	StatusDto saveBeacon(BeaconDto beaconDto);
	
	BeaconListResponseDto findAll();
	
	StatusDto updateBeacon(BeaconDto beaconDto);
	
	Status deleteById(Integer beaconId);
}
