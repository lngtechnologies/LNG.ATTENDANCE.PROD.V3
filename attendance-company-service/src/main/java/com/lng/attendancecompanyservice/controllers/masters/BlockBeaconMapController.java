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
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapDto;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapListResponse;

import status.StatusDto;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping(value="/master/block/beacon/map")
public class BlockBeaconMapController {
	
	@Autowired
	BlockBeaconMapService blockBeaconMapService;
	
	@PostMapping(value = "/create")
	public ResponseEntity<StatusDto> save(@RequestBody BlockBeaconMapDto blockBeaconMapDto) {
		StatusDto statusDto = blockBeaconMapService.saveBlkBeaconMap(blockBeaconMapDto);
		if (statusDto !=null){
			return new ResponseEntity<StatusDto>(statusDto, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "/findAll")
	public ResponseEntity<BlockBeaconMapListResponse> findAll() {
		BlockBeaconMapListResponse blockBeaconMapListResponse = blockBeaconMapService.findAll(); 
		return new ResponseEntity<BlockBeaconMapListResponse>(blockBeaconMapListResponse, HttpStatus.OK);
	}

	@PostMapping(value = "/update")
	public ResponseEntity<StatusDto> updateCustomer(@RequestBody BlockBeaconMapDto blockBeaconMapDto) {
		StatusDto statusDto = blockBeaconMapService.update(blockBeaconMapDto);
		if (statusDto != null) {
			return new ResponseEntity<StatusDto>(statusDto, HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	@PostMapping(value = "/findByCustId")
	public ResponseEntity<BlockBeaconMapListResponse> findByCustId(@RequestBody BlockBeaconMapDto blockBeaconMapDto) {
		BlockBeaconMapListResponse blockBeaconMapListResponse = blockBeaconMapService.findByCustId(blockBeaconMapDto.getCustId());
		return new ResponseEntity<BlockBeaconMapListResponse>(blockBeaconMapListResponse, HttpStatus.OK);
	}
	
	@PostMapping(value = "/delete")
	public ResponseEntity<StatusDto> delete(@RequestBody BlockBeaconMapDto blockBeaconMapDto) {
		StatusDto statusDto = blockBeaconMapService.deleteBlockBeaconmap(blockBeaconMapDto);
		if (statusDto !=null){
			return new ResponseEntity<StatusDto>(statusDto, HttpStatus.CREATED);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/findAllUsedAndAvailableBeacond")
	public ResponseEntity<BlockBeaconMapResponse> findAllUsedAndAvailableBeacond(@RequestBody BlockBeaconMapDto blockBeaconMapDto) {
		BlockBeaconMapResponse blockBeaconMapResponse = blockBeaconMapService.mapBeacons(blockBeaconMapDto);
		return new ResponseEntity<BlockBeaconMapResponse>(blockBeaconMapResponse, HttpStatus.OK);
	}
	
}