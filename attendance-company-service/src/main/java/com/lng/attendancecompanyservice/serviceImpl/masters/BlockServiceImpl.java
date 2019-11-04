package com.lng.attendancecompanyservice.serviceImpl.masters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.masters.Block;
import com.lng.attendancecompanyservice.entity.masters.Branch;
import com.lng.attendancecompanyservice.repositories.masters.BlockRepository;
import com.lng.attendancecompanyservice.repositories.masters.BranchRepository;
import com.lng.attendancecompanyservice.repositories.masters.EmployeeBlockRepository;
import com.lng.attendancecompanyservice.service.masters.BlockService;
import com.lng.dto.customer.BranchDto;
import com.lng.dto.masters.block.BlockDto;
import com.lng.dto.masters.block.BlockResponse;

import status.Status;
@Service
public class BlockServiceImpl implements BlockService {

	ModelMapper modelMapper=new ModelMapper();
	@Autowired
	BlockRepository blockRepository;
	@Autowired
	EmployeeBlockRepository employeeBlockRepository;
	@Autowired
	BranchRepository branchRepository;

	@Override
	public BlockResponse saveBlock(BlockDto blockDto) {
		BlockResponse response = new BlockResponse();
		// BlockDto blockDto2 = new BlockDto();
		Block  block = new Block();
		try{
			if(blockDto.getBlkLogicalName() == null || blockDto.getBlkLogicalName().isEmpty()) throw new Exception("Please enter Block name");
			int b = blockRepository.findByRefBranchIdAndBlkLogicalName(blockDto.getRefBranchId(), blockDto.getBlkLogicalName());
			//blockDto.setBlkCreatedDate(new Date());
			if(b == 0) {
				Branch branch = branchRepository.findBranchByBrId(blockDto.getRefBranchId());
				if(branch != null) {

					block.setBranch(branch);
					block.setBlkGPSRadius(blockDto.getBlkGPSRadius());
					block.setBlkLogicalName(blockDto.getBlkLogicalName());
					block.setBlkLatLong(blockDto.getBlkLatLong());
					block.setBlkCreatedDate(new Date());
					block.setBlkIsActive(true);
					blockRepository.save(block);
					response.status = new Status(false,200, "successfully created");
				}
				else{ 
					response.status = new Status(true,400, "BranchId Not Found");
				}
			}
			else{ 
				response.status = new Status(true,400,"BlockName already exist");
			}


		}catch(Exception ex){
			response.status = new Status(true,3000, ex.getMessage()); 
		}

		return response;
	}

	@Override
	public BlockResponse getAll() {
		//BlockDto blockDto1=new BlockDto();
		BlockResponse response = new BlockResponse();
		try {
			List<Block> blockList=blockRepository.findAll();
			response.setData1(blockList.stream().map(block -> convertToBlockDto(block)).collect(Collectors.toList()));
			response.status = new Status(false,200, "successfully  GetAll");
		}catch(Exception e) {
			response.status = new Status(true,3000, e.getMessage()); 

		}
		return response;
	}



	@Override
	public Status updateBlockByBlkId(BlockDto blockDto) {
		Status status = null;
		try {
			if(blockDto.getBlkLogicalName() == null || blockDto.getBlkLogicalName().isEmpty()) throw new Exception("Please enter Block name");
			if(blockDto.getBlkId() == null || blockDto.getBlkId() == 0) throw new Exception("Block id is null or zero");
			if(blockDto.getRefBranchId() == null || blockDto.getRefBranchId() == 0) throw new Exception("RefCoustomerId id is null or zero");
			Block block = blockRepository.findBlockByblkId(blockDto.getBlkId())	;	
			Branch branch = branchRepository.findBranchByBrId(blockDto.getRefBranchId());
			if(branch != null) {
				Block bl = blockRepository.findBlockhByblkLogicalNameAndBranch_brId(blockDto.getBlkLogicalName(), blockDto.getRefBranchId());
				if(bl == null) {
					block = modelMapper.map(blockDto,Block.class);
					block.setBranch(branch);
					block.setBlkCreatedDate(new Date());
					block.setBlkIsActive(false);
					blockRepository.save(block);
					status = new Status(false, 200, "Updated successfully");
				} else if (bl.getBlkId() == blockDto.getBlkId()) { 
					block = modelMapper.map(blockDto,Block.class);
					block.setBranch(branch);
					block.setBlkCreatedDate(new Date());
					block.setBlkIsActive(false);
					blockRepository.save(block);
					status = new Status(false, 200, "Updated successfully");
				}
				else{ 
					status = new Status(true,400,"BlockName already exist for Customer :" + blockDto.getRefBranchId());
				}
			}

			else {
				status = new Status(false, 200, "BranchId Not Found");

			}
		}
		catch(Exception e) {
			status = new Status(true, 4000, e.getMessage());
		}
		return status;
	}


	@Override
	public BlockResponse deleteByBlkId(Integer blkId) {
		BlockResponse response=new BlockResponse();
		Block block = new Block();
		try {

			block = blockRepository.findBlockByblkId(blkId);
			int a =blockRepository.findBlockBeaconMapByBlockBlkId(blkId);
			int b = blockRepository.findEmployeeBlockByBlockBlkId(blkId);
			if(block != null) {
				if(a == 0 && b == 0) {
					blockRepository.delete(block);					
					response.status = new Status(false,200, "successfully deleted");
					
				}else  {
					block.setBlkIsActive(false);
					blockRepository.save(block);
					response.status = new Status(false,200, "Block is used in other transaction, so it is set to isActive 0");
				}
			}else {
				response.status = new Status(true,400, "BlockId Not Found");
			}
		}catch(Exception e) { 
			response.status = new Status(true,500, e.getMessage());
		}
		return response;
	}


	public BlockDto convertToBlockDto(Block block) {
		BlockDto blockDto = modelMapper.map(block,BlockDto.class);
		blockDto.setBlkId(block.getBlkId());
		blockDto.setCustId(blockDto.getCustId());
		blockDto.setRefBranchId(block.getBranch().getBrId());
		blockDto.setBrName(block.getBranch().getBrName());
		BranchDto branchDto = modelMapper.map(block.getBranch(),BranchDto.class);
		return blockDto;
	}

	@Override
	public BlockResponse getBranchDetailsByCustId(Integer custId) {
		//BlockDto blockDto = new BlockDto();
		BlockResponse response=new BlockResponse(); 
		List<BlockDto> blockDtoList = new ArrayList<>();
		try {

			List<Object[]> blockList = blockRepository.findBranchDetailsByCustomer_CustId(custId);

			for (Object[] p : blockList) {	
				BlockDto blockDto1 = new BlockDto();
				blockDto1.setCustId(Integer.valueOf(p[0].toString()));
				blockDto1.setBrId(Integer.valueOf(p[1].toString()));
				blockDto1.setBrCode(p[2].toString());
				blockDto1.setBrName(p[3].toString());
				blockDtoList.add(blockDto1);
				response.status = new Status(false,200, "successfully GetSBranchDetails");

			}


		}catch (Exception e){
			response.status = new Status(true,4000,e.getMessage());


		}
		response.setData1(blockDtoList);
		return response;
	}



	@Override
	public BlockResponse getBlockDetailsByCustIdANDRefBranchId(int custId,int refBranchId) { 
		//BlockDto blockDto = new BlockDto();
		BlockResponse response=new BlockResponse(); 
		List<BlockDto> blockDtoList = new ArrayList<>();
		try {
			List<Object[]> blockList = blockRepository.findBlockDetailsByCustomer_CustIdAndBranch_RefBranchId( custId, refBranchId);

			for (Object[] p : blockList) {

				BlockDto blockDto1 = new BlockDto();
				blockDto1.setBlkId(Integer.valueOf(p[0].toString()));
				blockDto1.setBlkLogicalName(p[1].toString());
				blockDto1.setBlkGPSRadius(Integer.valueOf(p[2].toString()));
				blockDto1.setBlkLatLong(p[3].toString());
				blockDto1.setCustId(Integer.valueOf(p[4].toString()));
				blockDtoList.add(blockDto1);
				response.status = new Status(false,200, "successfully GetSBlockhDetails");

			}


		}catch (Exception e){
			response.status = new Status(true,4000,e.getMessage());


		}
		response.setData1(blockDtoList);
		return response;
	}

	@Override
	public BlockResponse getBlockByBlkId(Integer blkId) {
		BlockResponse response = new BlockResponse();
		try {
			Block block=blockRepository.findBlockByblkId(blkId);
			if(block != null) {
				BlockDto blockDto = convertToBlockDto(block);
				response.data = blockDto;
				response.status = new Status(false,200, "successfully  GetBlockDetails");
			}
			else {
				response.status = new Status(true, 4000, "Not found");
			}
		}catch(Exception e) {
			response.status = new Status(true,3000, e.getMessage()); 

		}
		return response;
	}
}
