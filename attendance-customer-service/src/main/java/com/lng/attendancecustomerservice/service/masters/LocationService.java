package com.lng.attendancecustomerservice.service.masters;

import com.lng.dto.masters.location.LocationDto;
import com.lng.dto.masters.location.LocationResponse;

public interface LocationService {
	
	LocationResponse  save(LocationDto locationDto);
	
	LocationResponse  updateByLocationId(LocationDto locationDto);
	
	LocationResponse  deleteByLocationId(Integer locationId);
	
	LocationResponse getAllByCustId(Integer refCustId);

}
