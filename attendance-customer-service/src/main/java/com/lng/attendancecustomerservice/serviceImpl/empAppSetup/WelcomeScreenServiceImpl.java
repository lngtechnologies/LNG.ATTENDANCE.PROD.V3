package com.lng.attendancecustomerservice.serviceImpl.empAppSetup;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.BlockBeaconMap;
import com.lng.attendancecustomerservice.entity.masters.Contractor;
import com.lng.attendancecustomerservice.repositories.empAppSetup.WelcomeScreenRepository;
import com.lng.attendancecustomerservice.service.empAppSetup.WelcomeScreenService;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapDto;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapListResponse;

import status.Status;

@Service
public class WelcomeScreenServiceImpl implements WelcomeScreenService {

	ModelMapper modelMapper = new ModelMapper();
	
	@Autowired
	WelcomeScreenRepository welcomeScreenRepository;
	
	
	@Override
	public BlockBeaconMapListResponse getBeaconsByEmpId(Integer empId) {
		BlockBeaconMapListResponse response = new BlockBeaconMapListResponse();
		try {
			
			List<BlockBeaconMap> blockBeaconMapList = welcomeScreenRepository.findByEmployee_EmpId(empId);
			response.setBeaconMapDtolist(blockBeaconMapList.stream().map(blockBeaconMap -> convertToBlockBeaconMapDto(blockBeaconMap)).collect(Collectors.toList()));
			if(!response.getBeaconMapDtolist().isEmpty()) {
				response.status = new Status(false,200, "Success");
			} else {
				response.status = new Status(true,400, "No beacons mapped for this employee"); 
			}
			
		}catch(Exception e) {
			response.status = new Status(true,500, e.getMessage()); 

		}
		return response;
		
	}

	public BlockBeaconMapDto convertToBlockBeaconMapDto(BlockBeaconMap blockBeaconMap) {
		BlockBeaconMapDto blockBeaconMapDto = modelMapper.map(blockBeaconMap,BlockBeaconMapDto.class);
		blockBeaconMapDto.setRefBlkId(blockBeaconMap.getBlock().getBlkId());
		blockBeaconMapDto.setBlkLogicalName(blockBeaconMap.getBlock().getBlkLogicalName());
		blockBeaconMapDto.setBrId(blockBeaconMap.getBlock().getBranch().getBrId());
		blockBeaconMapDto.setCustId(blockBeaconMap.getBlock().getBranch().getCustomer().getCustId());
		return blockBeaconMapDto;
	}

}
