package com.lng.attendancecustomerservice.controllers.masters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecustomerservice.service.masters.LocationService;
import com.lng.dto.masters.location.LocationDto;
import com.lng.dto.masters.location.LocationResponse;

@RestController
@CrossOrigin(origins = "*", maxAge=3600)
@RequestMapping(value="/master/location")
public class LocationController {
	
	@Autowired
	LocationService locationService;
	
	@PostMapping(value = "/create")
	public ResponseEntity<LocationResponse> save(@RequestBody LocationDto locationDto) {
		LocationResponse locationResponse = locationService.save(locationDto);
		if (locationDto !=null){
			return new ResponseEntity<LocationResponse>(locationResponse, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	

	@PostMapping(value = "/update")
	public ResponseEntity<LocationResponse> update(@RequestBody LocationDto locationDto) {
		LocationResponse locationResponse = locationService.updateByLocationId(locationDto);
		if (locationDto !=null){
			return new ResponseEntity<LocationResponse>(locationResponse, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	@PostMapping(value = "/deleteByLocationId")
	public ResponseEntity<LocationResponse> delete(@RequestBody LocationDto locationDto) {
		LocationResponse locationResponse = locationService.deleteByLocationId(locationDto.getLocationId());
		if (locationDto !=null){
			return new ResponseEntity<LocationResponse>(locationResponse, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/getAllByCustId")
	public ResponseEntity<LocationResponse> findByCustId(@RequestBody LocationDto locationDto) {
		LocationResponse locationResponse = locationService.getAllByCustId(locationDto.getRefCustId());
		return new ResponseEntity<LocationResponse>(locationResponse, HttpStatus.OK);
	}

}
