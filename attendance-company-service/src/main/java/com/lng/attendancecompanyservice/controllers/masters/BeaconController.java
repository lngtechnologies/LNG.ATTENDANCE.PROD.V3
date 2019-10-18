package com.lng.attendancecompanyservice.controllers.masters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lng.attendancecompanyservice.service.masters.BeaconService;
import com.lng.dto.masters.beacon.BeaconDto;
import com.lng.dto.masters.beacon.BeaconListResponseDto;

import status.StatusDto;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping(value="/beacon")
public class BeaconController {

	@Autowired
	BeaconService beaconService;

	@PostMapping(value = "/create")
	public ResponseEntity<StatusDto> save(@RequestBody BeaconDto beaconDto) {
		StatusDto statusDto = beaconService.saveBeacon(beaconDto);
		if (statusDto !=null){
			return new ResponseEntity<StatusDto>(statusDto, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "/findAll")
	public ResponseEntity<BeaconListResponseDto> findAll() {
		BeaconListResponseDto beaconListResponseDto = beaconService.findAll(); 
		if(beaconListResponseDto.getBeaconDtoList().isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<BeaconListResponseDto>(beaconListResponseDto, HttpStatus.OK);
	}

	@PostMapping(value = "/update")
	public ResponseEntity<StatusDto> updateCustomer(@RequestBody BeaconDto beaconDto) {
		StatusDto statusDto = beaconService.updateBeacon(beaconDto);
		if (statusDto != null) {
			return new ResponseEntity<StatusDto>(statusDto, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}