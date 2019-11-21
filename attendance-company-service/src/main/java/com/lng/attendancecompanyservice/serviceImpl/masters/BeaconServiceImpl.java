package com.lng.attendancecompanyservice.serviceImpl.masters;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.masters.Beacon;
import com.lng.attendancecompanyservice.entity.masters.BlockBeaconMap;
import com.lng.attendancecompanyservice.repositories.masters.BeaconRepository;
import com.lng.attendancecompanyservice.repositories.masters.BlockBeaconMapRepository;
import com.lng.attendancecompanyservice.service.masters.BeaconService;
import com.lng.dto.masters.beacon.BeaconDto;
import com.lng.dto.masters.beacon.BeaconListResponseDto;

import status.Status;
import status.StatusDto;

@Service
public class BeaconServiceImpl implements BeaconService {

	@Autowired
	BeaconRepository beaconRepository;
	
	@Autowired
	BlockBeaconMapRepository blockBeaconMapRepository;
	
	ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public StatusDto saveBeacon(BeaconDto beaconDto) {
		StatusDto statusDto = new StatusDto();
		
		Beacon beacon = beaconRepository.findBeaconByBeaconCode(beaconDto.getBeaconCode());
		try {
			if(beacon == null) {
				beacon = modelMapper.map(beaconDto, Beacon.class);
				beacon.setBeaconCreatedDate(new Date());
				beaconRepository.save(beacon);
				statusDto.setCode(200);
				statusDto.setError(false);
				statusDto.setMessage("Successfully Saved");
			}else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("Beacon Code is Already Exist");
			}
		}catch (Exception e) {
			statusDto.setCode(500);
			statusDto.setError(true);
			statusDto.setMessage("Opps...! Something Went Wrong!");
		}
		
		return statusDto;
	}
	
	@Override
	public BeaconListResponseDto findAll() {
		BeaconListResponseDto beaconListResponseDto = new BeaconListResponseDto();
		try {
			List<Beacon> beaconDtoList =  beaconRepository.findAll();

			beaconListResponseDto.setBeaconDtoList(beaconDtoList.stream().map(beacon -> convertToBeaconDto(beacon)).collect(Collectors.toList()));

			if(beaconListResponseDto != null && beaconListResponseDto.getBeaconDtoList() != null) {

				beaconListResponseDto.status = new Status(false, 2000, "Success");
			}else {
				beaconListResponseDto.status = new Status(true, 4000, "Not Found");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			beaconListResponseDto.status = new Status(true, 5000, "Oops...! Something went wrong!");
		}
		return beaconListResponseDto;
	}
	
	@Override
	public StatusDto updateBeacon(BeaconDto beaconDto) {
		
		StatusDto statusDto = new StatusDto();
		
		Beacon beacon1 = beaconRepository.findBeaconByBeaconId(beaconDto.getBeaconId());
		Beacon beacon2 = beaconRepository.findBeaconByBeaconCode(beaconDto.getBeaconCode());
		BlockBeaconMap blockBeaconMap = blockBeaconMapRepository.findByBeaconCodeAndBlkBeaconMapIsActive(beacon1.getBeaconCode(), true);
		try {
			if(beacon1 != null){
				if(beacon2 == null || (beacon1.getBeaconId() == beaconDto.getBeaconId() && beacon1.getBeaconCode().equals(beaconDto.getBeaconCode()))) {
					beacon1.setBeaconCode(beaconDto.getBeaconCode());
					beacon1.setBeaconCreatedDate(new Date());
					beaconRepository.save(beacon1);
					if(blockBeaconMap != null) {
						blockBeaconMap.setBeaconCode(beaconDto.getBeaconCode());
						blockBeaconMapRepository.save(blockBeaconMap);
					}
					
					statusDto.setCode(200);
					statusDto.setError(false);
					statusDto.setMessage("Successfully Updated");
				} else {
					statusDto.setCode(400);
					statusDto.setError(true);
					statusDto.setMessage("Beacon Code Already Exist");
				}
			}else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("Beacon Not Found");
			}

		}catch (Exception e) {
			statusDto.setCode(500);
			statusDto.setError(true);
			statusDto.setMessage("Opps...! Somenthing Went Wrong!");
		}
		return statusDto;
	}
	
	@SuppressWarnings("unused")
	@Override
	public Status deleteById(Integer beaconId) {
		Status status = null;
		try {
		Beacon beacon = beaconRepository.findBeaconByBeaconId(beaconId);
		BlockBeaconMap blockBeaconMap = blockBeaconMapRepository.findByBeaconCodeAndBlkBeaconMapIsActive(beacon.getBeaconCode(), true);
		
		if(beacon != null) {
			if(blockBeaconMap == null) {
				beaconRepository.delete(beacon);
				status = new Status(false, 200, "Successfully deleted");
			}else {
				status = new Status(true, 400, "The record has been disabled, since it has been used in another transactions");
			}
		}else {
			status = new Status(true, 400, "Beacon not found");
		}
			
		} catch (Exception e) {
			status = new Status(true, 500, "Opps..! Something went wrong");
		}	
		return status;
	}

	
	
	public BeaconDto convertToBeaconDto(Beacon beacon) {
		BeaconDto  beaconDto = modelMapper.map(beacon, BeaconDto.class);
		return beaconDto;
	}	
}
