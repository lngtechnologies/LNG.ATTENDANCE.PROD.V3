package com.lng.attendancecompanyservice.serviceImpl.masters;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.masters.Block;
import com.lng.attendancecompanyservice.entity.masters.BlockBeaconMap;
import com.lng.attendancecompanyservice.repositories.masters.BlockBeaconMapRepository;
import com.lng.attendancecompanyservice.repositories.masters.BlockRepository;
import com.lng.attendancecompanyservice.service.masters.BlockBeaconMapService;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapDto;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapListResponse;

import status.Status;
import status.StatusDto;

@Service
public class BlockBeaconMapServiceImpl implements BlockBeaconMapService {
	
	@Autowired
	BlockBeaconMapRepository blockBeaconMapRepository;
	
	@Autowired
	BlockRepository blockRepository;
	
	ModelMapper modelMapper = new ModelMapper();

	@Override
	public StatusDto saveBlkBeaconMap(BlockBeaconMapDto blockBeaconMapDto) {
		StatusDto statusDto = new StatusDto();
		
		Block block = blockRepository.findByBlkId(blockBeaconMapDto.getRefBlkId());
		BlockBeaconMap blockBeaconMap1 = blockBeaconMapRepository.findBlockBeaconMapByBeaconCode(blockBeaconMapDto.getBeaconCode());
		try {
			if(block != null) {
				if(blockBeaconMap1 == null) {
					BlockBeaconMap blockBeaconMap = modelMapper.map(blockBeaconMapDto, BlockBeaconMap.class);
					blockBeaconMap.setBlkBeaconMapCreatedDate(new Date());
					blockBeaconMap.setBlkBeaconMapIsActive(true);
					blockBeaconMapRepository.save(blockBeaconMap);
					statusDto.setCode(200);
					statusDto.setError(false);
					statusDto.setMessage("Successfully Saved");
				}else {
					statusDto.setCode(400);
					statusDto.setError(true);
					statusDto.setMessage("Beacon Code Type Already Exist");
				}
			}else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("Block id cannot be null");
			}
		}catch (Exception e) {
			statusDto.setCode(500);
			statusDto.setError(true);
			statusDto.setMessage("Opps...! Something Went Wrong!");
		}
		return statusDto;
	}

	@Override
	public BlockBeaconMapListResponse findAll() {
		BlockBeaconMapListResponse blockBeaconMapListResponse = new BlockBeaconMapListResponse();
		try {
			List<BlockBeaconMap> blockBeaconMapList =  blockBeaconMapRepository.findAll();

			blockBeaconMapListResponse.setBeaconMapDtolist(blockBeaconMapList.stream().map(blockBeaconMap -> convertToIndustryTypeDto(blockBeaconMap)).collect(Collectors.toList()));

			if(blockBeaconMapListResponse != null && blockBeaconMapListResponse.getBeaconMapDtolist() != null) {

				blockBeaconMapListResponse.status = new Status(false, 2000, "Success");
			}else {
				blockBeaconMapListResponse.status = new Status(true, 4000, "Not Found");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			blockBeaconMapListResponse.status = new Status(true, 5000, "Oops...! Something went wrong!");
		}
		return blockBeaconMapListResponse;
	}

	@Override
	public StatusDto update(BlockBeaconMapDto blockBeaconMapDto) {
		StatusDto statusDto = new StatusDto();
		
		BlockBeaconMap blockBeaconMap1 = blockBeaconMapRepository.findBlockBeaconMapByblkBeaconMapId(blockBeaconMapDto.getBlkBeaconMapId());
		BlockBeaconMap blockBeaconMap2 = blockBeaconMapRepository.findBlockBeaconMapByBeaconCode(blockBeaconMapDto.getBeaconCode());
		Block block = blockRepository.findByBlkId(blockBeaconMapDto.getRefBlkId());
		try {
			if(blockBeaconMap1 != null && block != null) {
				if(blockBeaconMap2 == null) {
					blockBeaconMap1.setBeaconCode(blockBeaconMapDto.getBeaconCode());
					blockBeaconMap1.setBlock(block);
					blockBeaconMap1.setBlkBeaconMapCreatedDate(new Date());
					blockBeaconMap1.setBlkBeaconMapIsActive(true);
					statusDto.setCode(200);
					statusDto.setError(false);
					statusDto.setMessage("Successfully Updated");
				}else {
					statusDto.setCode(400);
					statusDto.setError(true);
					statusDto.setMessage("Beacon Code Already Exist");
				}
			}else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("BlockBeacon Id or Block Id Not Found");
			}
		}catch (Exception e) {
			statusDto.setCode(500);
			statusDto.setError(true);
			statusDto.setMessage("Opps...! Somenthing Went Wrong!");
		}
		
		return statusDto;
	}

	public BlockBeaconMapDto convertToIndustryTypeDto(BlockBeaconMap blockBeaconMap) {
		BlockBeaconMapDto  blockBeaconMapDto = modelMapper.map(blockBeaconMap, BlockBeaconMapDto.class);
		blockBeaconMapDto.setBlkLogicalName(blockBeaconMap.getBlock().getBlkLogicalName());
		return blockBeaconMapDto;
	}
}
