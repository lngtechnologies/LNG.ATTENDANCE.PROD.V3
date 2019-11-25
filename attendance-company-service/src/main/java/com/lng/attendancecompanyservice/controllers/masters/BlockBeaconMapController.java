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

import com.lng.attendancecompanyservice.service.masters.BlockBeaconMapService;
import com.lng.dto.masters.beacon.BlockBeaconMapResponse;
import com.lng.dto.masters.beaconBlockMap.BlockAndBeaconCodeMapDto;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapDto;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapList;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapListResponse;

import status.StatusDto;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping(value="/master/block/beacon/map")
public class BlockBeaconMapController {

	@Autowired
	BlockBeaconMapService blockBeaconMapService;

	@PostMapping(value = "/create")
	public ResponseEntity<StatusDto> save(@RequestBody BlockBeaconMapList blockBeaconMapDto) {
		StatusDto statusDto = blockBeaconMapService.saveBlkBeaconMap(blockBeaconMapDto);
		if (statusDto !=null){
			return new ResponseEntity<StatusDto>(statusDto, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	/*@GetMapping(value = "/findAll")
	public ResponseEntity<BlockBeaconMapListResponse> findAll() {
		BlockBeaconMapListResponse blockBeaconMapListResponse = blockBeaconMapService.findAll(); 
		if(blockBeaconMapListResponse.getBeaconMapDtolist().isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<BlockBeaconMapListResponse>(blockBeaconMapListResponse, HttpStatus.OK);
	}*/
	
	@GetMapping(value = "/findAll")
	public ResponseEntity<BlockAndBeaconCodeMapDto> findAll() {
		BlockAndBeaconCodeMapDto blockBeaconMapListResponse = blockBeaconMapService.findAll(); 
		return new ResponseEntity<BlockAndBeaconCodeMapDto>(blockBeaconMapListResponse, HttpStatus.OK);
	}

	@PostMapping(value = "/update")
	public ResponseEntity<StatusDto> updateCustomer(@RequestBody BlockBeaconMapList blockBeaconMapList) {
		StatusDto statusDto = blockBeaconMapService.update(blockBeaconMapList);
		if (statusDto != null) {
			return new ResponseEntity<StatusDto>(statusDto, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	/*@PostMapping(value = "/getAllByCustId")
	public ResponseEntity<BlockBeaconMapListResponse> edit2(@RequestBody BlockBeaconMapDto blockBeaconMapDto) {
		BlockBeaconMapListResponse blockResponse = blockBeaconMapService.findByCustId(blockBeaconMapDto.getCustId());
		if (blockResponse != null) {
			return new ResponseEntity<BlockBeaconMapListResponse>(blockResponse, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}*/
	
	@PostMapping(value = "/getAllByCustId")
	public ResponseEntity<BlockAndBeaconCodeMapDto> edit2(@RequestBody BlockBeaconMapDto blockBeaconMapDto) {
		BlockAndBeaconCodeMapDto blockResponse = blockBeaconMapService.findByCustId(blockBeaconMapDto.getCustId());
		if (blockResponse != null) {
			return new ResponseEntity<BlockAndBeaconCodeMapDto>(blockResponse, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "/getAllAvailableAndUsedBeacons")
	public ResponseEntity<BlockBeaconMapResponse> getAllAvailableAndUsedBeacons(@RequestBody BlockBeaconMapDto blockBeaconMapDto) {
		BlockBeaconMapResponse blockResponse = blockBeaconMapService.mapBeacons(blockBeaconMapDto);
		if (blockResponse != null) {
			return new ResponseEntity<BlockBeaconMapResponse>(blockResponse, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}


