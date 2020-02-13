package com.lng.attendancecompanyservice.serviceImpl.masters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

	private final Lock displayQueueLock = new ReentrantLock();

	@Override
	public StatusDto saveBeacon(BeaconDto beaconDto) {
		StatusDto statusDto = new StatusDto();
		final Lock displayLock = this.displayQueueLock; 

		Beacon beacon = beaconRepository.findBeaconByBeaconCode(beaconDto.getBeaconCode());
		Beacon beacon1 = beaconRepository.findBeaconByBeaconCodeAndBeaconIsActive(beaconDto.getBeaconCode(), false);
		try {
			displayLock.lock();
			if(beacon == null) {
				beacon = modelMapper.map(beaconDto, Beacon.class);
				beacon.setBeaconCreatedDate(new Date());
				beacon.setBeaconIsActive(true);
				beaconRepository.save(beacon);
				statusDto.setCode(200);
				statusDto.setError(false);
				statusDto.setMessage("created");

			}else if (beacon1!= null){
				beacon1.setBeaconIsActive(true);
				beacon1.setBeaconCreatedDate(new Date());
				beaconRepository.save(beacon1);
				statusDto.setCode(200);
				statusDto.setError(false);
				statusDto.setMessage("created");
			}else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("Beacon code is already exist");
			}
		}catch (Exception e) {
			statusDto.setCode(500);
			statusDto.setError(true);
			statusDto.setMessage("Opps...! Something went wrong!");
		}
		finally {
			displayLock.unlock();
		}
		return statusDto;
	}

	@Override
	public BeaconListResponseDto findAll() {
		BeaconListResponseDto beaconListResponseDto = new BeaconListResponseDto();
		List<BeaconDto> beaconDtoList = new ArrayList<>();
	
		try {
			List<Object[]> beaconList =  beaconRepository.findAllUsedAndUnUsedBecon();
			if(beaconList.isEmpty()) {
				beaconListResponseDto.status = new Status(false,400, "Not found");
			}else {
					for (Object[] p : beaconList) {	
					BeaconDto beaconDto = new BeaconDto();
					beaconDto.setBeaconId(Integer.valueOf(p[0].toString()));
					beaconDto.setBeaconCode(p[1].toString());
					beaconDto.setBeaconCreatedDate((Date) p[2]);
					String value = p[4].toString();
					boolean b = Boolean.parseBoolean(value);
					beaconDto.setIsUsed(b);
					beaconDtoList.add(beaconDto);
					beaconListResponseDto.setBeaconDtoList(beaconDtoList);
					beaconListResponseDto.status = new Status(false,200, "Success");
				}
			}
		}catch (Exception e){
			beaconListResponseDto.status = new Status(true,500, "Oops..! Something went wrong..");


		}
		return beaconListResponseDto;
	}
	@SuppressWarnings("unused")
	@Override
	public StatusDto updateBeacon(BeaconDto beaconDto) {

		StatusDto statusDto = new StatusDto();
		final Lock displayLock = this.displayQueueLock; 

		Beacon beacon1 = beaconRepository.findBeaconByBeaconId(beaconDto.getBeaconId());
		Beacon beacon2 = beaconRepository.findBeaconByBeaconCode(beaconDto.getBeaconCode());
		List<BlockBeaconMap> blockBeaconMapList = blockBeaconMapRepository.getByBeaconCodeAndBlkBeaconMapIsActive(beacon1.getBeaconCode(), true);
		try {
			displayLock.lock();
			if(beacon1 != null){
				if(beacon2 == null || (beacon1.getBeaconId() == beaconDto.getBeaconId() && beacon1.getBeaconCode().equals(beaconDto.getBeaconCode()))) {
					beacon1.setBeaconCode(beaconDto.getBeaconCode());
					beacon1.setBeaconCreatedDate(new Date());
					beaconRepository.save(beacon1);
					
					if(!blockBeaconMapList.isEmpty()) {
						for(BlockBeaconMap blockBeaconMap: blockBeaconMapList) {
							blockBeaconMap.setBeaconCode(beaconDto.getBeaconCode());
							blockBeaconMapRepository.save(blockBeaconMap);
						}
					}
					statusDto.setCode(200);
					statusDto.setError(false);
					statusDto.setMessage("updated");

				} else {
					statusDto.setCode(400);
					statusDto.setError(true);
					statusDto.setMessage("Beacon code already exist");

				}
			}else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("Beacon not found");

			}

		}catch (Exception e) {
			statusDto.setCode(500);
			statusDto.setError(true);
			statusDto.setMessage("Opps...! Somenthing went wrong!");	
		}
		finally {
			displayLock.unlock();
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
					status = new Status(false, 200, "deleted");
				}else {
					beacon.setBeaconIsActive(false);
					beaconRepository.save(beacon);
					status = new Status(false, 200, "The record has been disabled since it has been used in other transactions");
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
