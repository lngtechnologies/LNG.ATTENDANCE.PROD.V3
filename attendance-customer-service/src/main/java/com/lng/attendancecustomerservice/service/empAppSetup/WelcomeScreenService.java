package com.lng.attendancecustomerservice.service.empAppSetup;

import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapListResponse;

public interface WelcomeScreenService {

	BlockBeaconMapListResponse getBeaconsByEmpId(Integer empId);
}
