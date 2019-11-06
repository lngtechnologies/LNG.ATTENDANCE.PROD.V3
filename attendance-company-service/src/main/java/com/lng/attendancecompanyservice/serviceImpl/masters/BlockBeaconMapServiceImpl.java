package com.lng.attendancecompanyservice.serviceImpl.masters;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.custOnboarding.Customer;
import com.lng.attendancecompanyservice.entity.masters.Beacon;
import com.lng.attendancecompanyservice.entity.masters.Block;
import com.lng.attendancecompanyservice.entity.masters.BlockBeaconMap;
import com.lng.attendancecompanyservice.repositories.custOnboarding.CustomerRepository;
import com.lng.attendancecompanyservice.repositories.masters.BeaconRepository;
import com.lng.attendancecompanyservice.repositories.masters.BlockBeaconMapRepository;
import com.lng.attendancecompanyservice.repositories.masters.BlockRepository;
import com.lng.attendancecompanyservice.service.masters.BlockBeaconMapService;
import com.lng.dto.masters.beacon.BeaconDto;
import com.lng.dto.masters.beacon.BlockBeaconMapResponse;
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

	@Autowired
	BeaconRepository beaconRepository;

	@Autowired
	CustomerRepository customerRepository;

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

			blockBeaconMapListResponse.setBeaconMapDtolist(blockBeaconMapList.stream().map(blockBeaconMap -> convertToBlockBeaconMapDto(blockBeaconMap)).collect(Collectors.toList()));

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
				if(blockBeaconMap2 == null || (blockBeaconMap1.getBlkBeaconMapId() == blockBeaconMapDto.getBlkBeaconMapId() && blockBeaconMap1.getBeaconCode().equals(blockBeaconMapDto.getBeaconCode()))) {
					blockBeaconMap1.setBeaconCode(blockBeaconMapDto.getBeaconCode());
					blockBeaconMap1.setBeaconType(blockBeaconMapDto.getBeaconType());
					blockBeaconMap1.setBlock(block);
					blockBeaconMap1.setBlkBeaconMapCreatedDate(new Date());
					blockBeaconMap1.setBlkBeaconMapIsActive(true);
					blockBeaconMapRepository.save(blockBeaconMap1);
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


	@Override
	public BlockBeaconMapListResponse findByCustId(Integer custId) {
		BlockBeaconMapListResponse blockBeaconMapListResponse = new BlockBeaconMapListResponse();

		try {
			Customer customer = customerRepository.findCustomerByCustId(custId);
			if(customer != null) {
				List<BlockBeaconMap> blockBeaconMapList =  blockBeaconMapRepository.findByCustomer_CustId(custId);

				blockBeaconMapListResponse.setBeaconMapDtolist(blockBeaconMapList.stream().map(blockBeaconMap -> convertToBlockBeaconMapDto(blockBeaconMap)).collect(Collectors.toList()));

				if(blockBeaconMapListResponse.getBeaconMapDtolist().isEmpty()) {
					blockBeaconMapListResponse.status = new Status(true, 4000, "Not Found");

				}else {
					blockBeaconMapListResponse.status = new Status(false, 2000, "Success");

				}
			}else {
				blockBeaconMapListResponse.status = new Status(true, 4000, "Not Found for this customer");
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			blockBeaconMapListResponse.status = new Status(true, 5000, "Oops...! Something went wrong!");
		}
		return blockBeaconMapListResponse;
	}


	@Override
	public BlockBeaconMapResponse mapBeacons(BlockBeaconMapDto blockBeaconMapDto) {
		BlockBeaconMapResponse blockBeaconMapResponse = new BlockBeaconMapResponse();
		try {
			List<BlockBeaconMap> usedBeaconList = blockBeaconMapRepository.findByBlock_BlkId(blockBeaconMapDto.getRefBlkId());
			blockBeaconMapResponse.setUsedBeacons(usedBeaconList.stream().map(blockBeaconMap -> convertToBlockBeaconMapDto(blockBeaconMap)).collect(Collectors.toList()));


			List<Beacon> availableBeaconList = beaconRepository.findAllAvailableBeacons();
			blockBeaconMapResponse.setAvailableBeacons(availableBeaconList.stream().map(beacon -> convertToBeaconDto(beacon)).collect(Collectors.toList()));

			if(blockBeaconMapResponse.getUsedBeacons().isEmpty()) {
				blockBeaconMapResponse.status = new Status(false, 200, "There is no beacons are used for this block");

			} else if(blockBeaconMapResponse.getAvailableBeacons().isEmpty())  {

				blockBeaconMapResponse.status = new Status(false, 200, "There is no available beacons, all are used");

			}else {

				blockBeaconMapResponse.status = new Status(false, 200, "Success");
			}
		} catch (Exception e) {
			blockBeaconMapResponse.status = new Status(true, 500, "Opps..! Sometging went wrong..");
		}

		return blockBeaconMapResponse;
	}

	public BlockBeaconMapDto convertToBlockBeaconMapDto(BlockBeaconMap blockBeaconMap) {
		BlockBeaconMapDto  blockBeaconMapDto = modelMapper.map(blockBeaconMap, BlockBeaconMapDto.class);
		blockBeaconMapDto.setBrId(blockBeaconMap.getBlock().getBranch().getBrId());
		blockBeaconMapDto.setRefBlkId(blockBeaconMap.getBlock().getBlkId());
		blockBeaconMapDto.setBlkLogicalName(blockBeaconMap.getBlock().getBlkLogicalName());
		blockBeaconMapDto.setCustId(blockBeaconMap.getBlock().getBranch().getCustomer().getCustId());
		return blockBeaconMapDto;
	}
	public BeaconDto convertToBeaconDto(Beacon beacon) {
		BeaconDto  beaconDto = modelMapper.map(beacon, BeaconDto.class);
		return beaconDto;
	}

	@Override
	public StatusDto deleteBlockBeaconmap(BlockBeaconMapDto blockBeaconMapDto) {
		StatusDto statusDto = new StatusDto();

		BlockBeaconMap blockBeaconMap = blockBeaconMapRepository.findBlockBeaconMapByblkBeaconMapId(blockBeaconMapDto.getBlkBeaconMapId());
		try {
			if(blockBeaconMap != null) {
				blockBeaconMapRepository.delete(blockBeaconMap);
				statusDto.setError(false);
				statusDto.setCode(200);					
				statusDto.setMessage("Successfully deleted");
			}else {
				statusDto.setError(true);
				statusDto.setCode(400);
				statusDto.setMessage("BlockBeaconMap Not Found");	
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusDto;
	}

}
