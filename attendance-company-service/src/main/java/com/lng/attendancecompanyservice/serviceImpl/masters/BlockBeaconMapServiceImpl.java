package com.lng.attendancecompanyservice.serviceImpl.masters;

import java.util.ArrayList;
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
import com.lng.dto.masters.beaconBlockMap.BlockAndBeaconCodeMapDto;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconCodeDto;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapDto;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapList;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapListResponse;
import com.lng.dto.masters.beaconBlockMap.BlockBeaconMapResponseDto;

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
	public StatusDto saveBlkBeaconMap(BlockBeaconMapList blockBeaconMapList) {
		StatusDto statusDto = new StatusDto();	
		String msg = null;
		String beacons ="";
		try {
			Block block = blockRepository.findBlockByblkId(blockBeaconMapList.getRefBlkId());
			if(block != null) {
				for(BlockBeaconMapDto BlockBeaconMapList : blockBeaconMapList.getBeacons()) {
					List<BlockBeaconMap> blockBeaconMap1 = blockBeaconMapRepository.findBlockBeaconMapByBeaconCodeAndBlkBeaconMapIsActive(BlockBeaconMapList.getBeaconCode(), true);

					if(blockBeaconMap1.isEmpty()) {
						BlockBeaconMap blockBeaconMap = modelMapper.map(BlockBeaconMapList, BlockBeaconMap.class);
						blockBeaconMap.setBlock(block);
						blockBeaconMap.setBlkBeaconMapCreatedDate(new Date());
						blockBeaconMap.setBlkBeaconMapIsActive(true);
						blockBeaconMapRepository.save(blockBeaconMap);
						statusDto.setCode(200);
						statusDto.setError(false);
						msg = "successfully created";
						//statusDto.setMessage("Successfully saved");
					}else {
						statusDto.setCode(400);
						statusDto.setError(true);
						msg = "Beacon already mapped";
						beacons += BlockBeaconMapList.getBeaconCode() + ","; 
						//statusDto.setMessage("Beacon already mapped");
					}
				}
				statusDto.setMessage(msg + " " + beacons);
			}else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("Block not found");

			}

		}catch (Exception e) {
			statusDto.setCode(500);
			statusDto.setError(true);
			statusDto.setMessage("Opps...! Something went wrong!");
		}
		return statusDto;
	}

	/*@Override
	public BlockBeaconMapListResponse findAll() {
		BlockBeaconMapListResponse blockBeaconMapListResponse = new BlockBeaconMapListResponse();
		try {
			List<BlockBeaconMap> blockBeaconMapList =  blockBeaconMapRepository.findAllByBlkBeaconMapIsActive(true);

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
	}*/

	@Override
	public BlockAndBeaconCodeMapDto findAll() {

		BlockAndBeaconCodeMapDto blockAndBeaconCodeMapDto = new BlockAndBeaconCodeMapDto();

		List<BlockBeaconMapResponseDto> blockBeaconMapLists = new ArrayList<>();

		try {
			List<Object[]> blockBeaconMapList =  blockBeaconMapRepository.findAllByBlkBeaconMapIsActive();

			for(Object[] p: blockBeaconMapList) {
				BlockBeaconMapResponseDto beaconMapResponse = new BlockBeaconMapResponseDto();

				List<BlockBeaconCodeDto> beaconCodeDtos = new ArrayList<>();

				beaconMapResponse.setRefBlkId(Integer.valueOf(p[0].toString()));
				beaconMapResponse.setBlkLogicalName(p[1].toString());
				beaconMapResponse.setBrId(Integer.valueOf(p[2].toString()));
				beaconMapResponse.setBrName(p[3].toString());
				beaconMapResponse.setCustId(Integer.valueOf(p[4].toString()));

				blockBeaconMapLists.add(beaconMapResponse);
				blockAndBeaconCodeMapDto.setBeaconMapResponseDtoList(blockBeaconMapLists);

				List<Object[]> beaconCodes =  blockBeaconMapRepository.findBeaconCodeByBlkId(beaconMapResponse.getRefBlkId());

				for(Object[] b: beaconCodes) {

					if(Integer.valueOf(b[1].toString()) == beaconMapResponse.getRefBlkId()) {
						BlockBeaconCodeDto beaconCodeDto = new BlockBeaconCodeDto();
						beaconCodeDto.setBlkBeaconMapId(Integer.valueOf(b[0].toString()));
						beaconCodeDto.setRefBlkId(Integer.valueOf(b[1].toString()));
						beaconCodeDto.setBeaconCode(b[2].toString());
						beaconCodeDto.setBeaconType(Integer.valueOf(b[3].toString()));

						beaconCodeDtos.add(beaconCodeDto);
					}
				}
				beaconMapResponse.setBeaconCodeDtoList(beaconCodeDtos);
			}

			if(!blockBeaconMapList.isEmpty()) {
				blockAndBeaconCodeMapDto.status = new Status(false, 200, "Success");
			}else {
				blockAndBeaconCodeMapDto.status = new Status(false, 400, "Not found");
			}
		}
		catch (Exception e) {

			blockAndBeaconCodeMapDto.status = new Status(true, 5000, "Oops...! Something went wrong!");
		}
		return blockAndBeaconCodeMapDto;
	}


	@Override
	public BlockAndBeaconCodeMapDto findByCustId(Integer custId) {

		BlockAndBeaconCodeMapDto blockAndBeaconCodeMapDto = new BlockAndBeaconCodeMapDto();

		List<BlockBeaconMapResponseDto> blockBeaconMapLists = new ArrayList<>();

		try {
			List<Object[]> blockBeaconMapList =  blockBeaconMapRepository.findAllByBlkBeaconMapIsActiveAndCustId(custId);

			for(Object[] p: blockBeaconMapList) {
				BlockBeaconMapResponseDto beaconMapResponse = new BlockBeaconMapResponseDto();

				List<BlockBeaconCodeDto> beaconCodeDtos = new ArrayList<>();

				beaconMapResponse.setRefBlkId(Integer.valueOf(p[0].toString()));
				beaconMapResponse.setBlkLogicalName(p[1].toString());
				beaconMapResponse.setBrId(Integer.valueOf(p[2].toString()));
				beaconMapResponse.setBrName(p[3].toString());
				beaconMapResponse.setCustId(Integer.valueOf(p[4].toString()));

				blockBeaconMapLists.add(beaconMapResponse);
				blockAndBeaconCodeMapDto.setBeaconMapResponseDtoList(blockBeaconMapLists);

				List<Object[]> beaconCodes =  blockBeaconMapRepository.findBeaconCodeByBlkId(beaconMapResponse.getRefBlkId());

				for(Object[] b: beaconCodes) {

					if(Integer.valueOf(b[1].toString()) == beaconMapResponse.getRefBlkId()) {
						BlockBeaconCodeDto beaconCodeDto = new BlockBeaconCodeDto();
						beaconCodeDto.setBlkBeaconMapId(Integer.valueOf(b[0].toString()));
						beaconCodeDto.setRefBlkId(Integer.valueOf(b[1].toString()));
						beaconCodeDto.setBeaconCode(b[2].toString());
						beaconCodeDto.setBeaconType(Integer.valueOf(b[3].toString()));

						beaconCodeDtos.add(beaconCodeDto);
					}
				}
				beaconMapResponse.setBeaconCodeDtoList(beaconCodeDtos);
			}

			if(!blockBeaconMapList.isEmpty()) {
				blockAndBeaconCodeMapDto.status = new Status(false, 200, "Success");
			}else {
				blockAndBeaconCodeMapDto.status = new Status(false, 400, "Not found");
			}
		}
		catch (Exception e) {

			blockAndBeaconCodeMapDto.status = new Status(true, 5000, "Oops...! Something went wrong!");
		}
		return blockAndBeaconCodeMapDto;
	}

	@Override
	public StatusDto update(BlockBeaconMapList blockBeaconMapList) {
		StatusDto statusDto = new StatusDto();	
		try {
			Block block = blockRepository.findBlockByblkId(blockBeaconMapList.getRefBlkId());
			if(block != null) {

				List<BlockBeaconMap> mapIdsAndBeaconsAndType = blockBeaconMapRepository.getBlockBeanMapByBlkId(blockBeaconMapList.getRefBlkId());

				for(BlockBeaconMap alreadyMapped: mapIdsAndBeaconsAndType) {
					BlockBeaconMap blockBeaconMap = blockBeaconMapRepository.findBlockBeaconMapByblkBeaconMapId(alreadyMapped.getBlkBeaconMapId());
					blockBeaconMap.setBlkBeaconMapIsActive(false);
					blockBeaconMapRepository.save(blockBeaconMap);
				}

				for(BlockBeaconMapDto updateBeaconMap : blockBeaconMapList.getBeacons()) 
				{
					if(updateBeaconMap.getBlkBeaconMapId() == null)
					{
						BlockBeaconMap blockBeaconMap = modelMapper.map(updateBeaconMap, BlockBeaconMap.class);
						blockBeaconMap.setBlock(block);
						blockBeaconMap.setBlkBeaconMapCreatedDate(new Date());
						blockBeaconMap.setBlkBeaconMapIsActive(true);
						blockBeaconMapRepository.save(blockBeaconMap);
					} 
					else if(updateBeaconMap.getBlkBeaconMapId() != null) {
						BlockBeaconMap blockBeaconMap = blockBeaconMapRepository.findBlockBeaconMapByblkBeaconMapId(updateBeaconMap.getBlkBeaconMapId());
						blockBeaconMap.setBeaconCode(updateBeaconMap.getBeaconCode());
						blockBeaconMap.setBeaconType(updateBeaconMap.getBeaconType());
						blockBeaconMap.setBlock(block);
						blockBeaconMap.setBlkBeaconMapCreatedDate(new Date());
						blockBeaconMap.setBlkBeaconMapIsActive(true);
						blockBeaconMapRepository.save(blockBeaconMap);
					}
				}

				statusDto.setCode(200);
				statusDto.setError(false);
				// msg = "Successfully Updates";
				statusDto.setMessage("successfully updated");
				//}

			}else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("Block not found");
			}
		}catch (Exception e) {
			statusDto.setCode(500);
			statusDto.setError(true);
			statusDto.setMessage("Opps...! Something went wrong!");
		}
		return statusDto;
	}

	/*@Override
	public StatusDto update(BlockBeaconMapDto blockBeaconMapDto) {
		StatusDto statusDto = new StatusDto();

		BlockBeaconMap blockBeaconMap1 = blockBeaconMapRepository.findBlockBeaconMapByblkBeaconMapId(blockBeaconMapDto.getBlkBeaconMapId());
		BlockBeaconMap blockBeaconMap2 = blockBeaconMapRepository.findByBeaconCode(blockBeaconMapDto.getBeaconCode());
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
	}*/


	/*@Override
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
	}*/


	@Override
	public BlockBeaconMapResponse mapBeacons(BlockBeaconMapDto blockBeaconMapDto) {
		BlockBeaconMapResponse blockBeaconMapResponse = new BlockBeaconMapResponse();
		try {
			List<BlockBeaconMap> usedBeaconList = blockBeaconMapRepository.findByBlock_BlkIdAndBlkBeaconMapIsActive(blockBeaconMapDto.getRefBlkId(), true);
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
				statusDto.setMessage("successfully deleted");
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

	@Override
	public BlockAndBeaconCodeMapDto findByCustIdAndBrId(Integer custId, Integer brId) {
		BlockAndBeaconCodeMapDto blockAndBeaconCodeMapDto = new BlockAndBeaconCodeMapDto();

		List<BlockBeaconMapResponseDto> blockBeaconMapLists = new ArrayList<>();

		try {
			List<Object[]> blockBeaconMapList =  blockBeaconMapRepository.findByCustomer_CustIdAndBranch_BrId(custId,brId);

			for(Object[] p: blockBeaconMapList) {
				BlockBeaconMapResponseDto beaconMapResponse = new BlockBeaconMapResponseDto();

				List<BlockBeaconCodeDto> beaconCodeDtos = new ArrayList<>();

				beaconMapResponse.setRefBlkId(Integer.valueOf(p[0].toString()));
				beaconMapResponse.setBlkLogicalName(p[1].toString());
				beaconMapResponse.setBrId(Integer.valueOf(p[2].toString()));
				beaconMapResponse.setBrName(p[3].toString());
				beaconMapResponse.setCustId(Integer.valueOf(p[4].toString()));

				blockBeaconMapLists.add(beaconMapResponse);
				blockAndBeaconCodeMapDto.setBeaconMapResponseDtoList(blockBeaconMapLists);

				List<Object[]> beaconCodes =  blockBeaconMapRepository.findBeaconCodeByBlkId(beaconMapResponse.getRefBlkId());

				for(Object[] b: beaconCodes) {

					if(Integer.valueOf(b[1].toString()) == beaconMapResponse.getRefBlkId()) {
						BlockBeaconCodeDto beaconCodeDto = new BlockBeaconCodeDto();
						beaconCodeDto.setBlkBeaconMapId(Integer.valueOf(b[0].toString()));
						beaconCodeDto.setRefBlkId(Integer.valueOf(b[1].toString()));
						beaconCodeDto.setBeaconCode(b[2].toString());
						beaconCodeDto.setBeaconType(Integer.valueOf(b[3].toString()));

						beaconCodeDtos.add(beaconCodeDto);
					}
				}
				beaconMapResponse.setBeaconCodeDtoList(beaconCodeDtos);
			}

			if(!blockBeaconMapList.isEmpty()) {
				blockAndBeaconCodeMapDto.status = new Status(false, 200, "Success");
			}else {
				blockAndBeaconCodeMapDto.status = new Status(false, 400, "Not found");
			}
		}
		catch (Exception e) {

			blockAndBeaconCodeMapDto.status = new Status(true, 5000, "Oops...! Something went wrong!");
		}
		return blockAndBeaconCodeMapDto;
	}



}
