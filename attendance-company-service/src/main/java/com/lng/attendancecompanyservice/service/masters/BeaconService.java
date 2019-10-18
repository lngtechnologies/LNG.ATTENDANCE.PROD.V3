package com.lng.attendancecompanyservice.service.masters;

import com.lng.dto.beacon.BeaconDto;
import com.lng.dto.beacon.BeaconListResponseDto;

import status.StatusDto;

public interface BeaconService {
	
	StatusDto saveBeacon(BeaconDto beaconDto);
	
	BeaconListResponseDto findAll();
	
	StatusDto updateBeacon(BeaconDto beaconDto);
}
