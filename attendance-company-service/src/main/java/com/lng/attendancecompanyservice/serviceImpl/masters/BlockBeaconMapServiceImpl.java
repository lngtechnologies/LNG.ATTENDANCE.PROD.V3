package com.lng.attendancecompanyservice.serviceImpl.masters;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.lng.attendancecompanyservice.entity.masters.BlockBeaconMap;
import com.lng.attendancecompanyservice.repositories.masters.BlockBeaconMapRepository;
import com.lng.attendancecompanyservice.service.masters.BlockBeaconMapService;
import com.lng.dto.blockBeaconMap.BlockBeaconMapDto;
import com.lng.dto.blockBeaconMap.BlockBeaconMapListResponse;

import status.StatusDto;

public class BlockBeaconMapServiceImpl implements BlockBeaconMapService {
	
	@Autowired
	BlockBeaconMapRepository blockBeaconMapRepository;
	
	ModelMapper modelMapper = new ModelMapper();

	@Override
	public StatusDto saveBlkBeaconMap(BlockBeaconMapDto blockBeaconMapDto) {
		StatusDto statusDto = new StatusDto();
		
		
		BlockBeaconMap blockBeaconMap = blockBeaconMapRepository.findBlockBeaconMapByBeaconCode(blockBeaconMapDto.getBeaconCode());
		try {
			if(blockBeaconMap == null) {
				
			}
		}catch (Exception e) {
			
		}
		return null;
	}

	@Override
	public BlockBeaconMapListResponse findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StatusDto update(BlockBeaconMapDto blockBeaconMapDto) {
		// TODO Auto-generated method stub
		return null;
	}

}
