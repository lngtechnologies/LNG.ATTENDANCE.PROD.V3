package com.lng.dto.masters.location;

import java.util.List;

import status.Status;

public class LocationResponse {

	public  List<LocationDto> locationDetails;  

	public Status status;

	public List<LocationDto> getLocationDetails() {
		return locationDetails;
	}

	public void setLocationDetails(List<LocationDto> locationDetails) {
		this.locationDetails = locationDetails;
	}



}
