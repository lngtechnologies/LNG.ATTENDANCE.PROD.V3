package com.lng.attendancecompanyservice.serviceImpl.masters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.custOnboarding.Customer;
import com.lng.attendancecompanyservice.entity.masters.Block;
import com.lng.attendancecompanyservice.entity.masters.Branch;
import com.lng.attendancecompanyservice.repositories.custOnboarding.CustomerRepository;
import com.lng.attendancecompanyservice.repositories.masters.BlockRepository;
import com.lng.attendancecompanyservice.repositories.masters.BranchRepository;
import com.lng.attendancecompanyservice.repositories.masters.EmployeeBlockRepository;
import com.lng.attendancecompanyservice.service.masters.BlockService;
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
	@Autowired
	CustomerRepository customerRepository;

	private final Lock displayQueueLock = new ReentrantLock();

	@Override
	public BlockResponse saveBlock(BlockDto blockDto) {
		BlockResponse response = new BlockResponse();
		final Lock displayLock = this.displayQueueLock; 
		Block  block = new Block();
		try{
			displayLock.lock();
			if(blockDto.getBlkLogicalName() == null || blockDto.getBlkLogicalName().isEmpty()) throw new Exception("Please enter Block name");
			int b = blockRepository.findByRefBranchIdAndBlkLogicalName(blockDto.getRefBranchId(), blockDto.getBlkLogicalName());
			if(b == 0) {
				Branch branch = branchRepository.findBranchByBrId(blockDto.getRefBranchId());
				if(branch != null) {

					block.setBranch(branch);
					block.setBlkGPSRadius(blockDto.getBlkGPSRadius());
					block.setBlkLogicalName(blockDto.getBlkLogicalName());
					block.setBlkLatitude(blockDto.getBlkLatitude());
					block.setBlkLongitude(blockDto.getBlkLongitude());
					block.setBlkCreatedDate(new Date());
					block.setBlkIsActive(true);
					blockRepository.save(block);
					response.status = new Status(false,200, "created");
					
				}
				else{ 
					response.status = new Status(true,400, "Branch not found");
					
				}
			}
			else{ 
				response.status = new Status(true,400,"Block name already exist");
				
			}

		}catch(Exception ex){
			response.status = new Status(true,500, "Oops..! Something went wrong..");
			
		}
		
		finally {
			displayLock.unlock();
		}

		return response;
	}

	@Override
	public BlockResponse getAll() {
		BlockResponse response = new BlockResponse();
		try {
			List<Block> blockList=blockRepository.findAll();
			response.setData1(blockList.stream().map(block -> convertToBlockDto(block)).collect(Collectors.toList()));
			if(response.getData1().isEmpty()) {
				response.status = new Status(false,400, "Not found"); 
			}else {
				response.status = new Status(false,200, "success");
			}
		}catch(Exception e) {
			response.status = new Status(true,500, "Oops..! Something went wrong.."); 

		}
		return response;
	}



	@Override
	public Status updateBlockByBlkId(BlockDto blockDto) {
		Status status = null;
		final Lock displayLock = this.displayQueueLock; 
		try {
			displayLock.lock();
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
					block.setBlkIsActive(true);
					blockRepository.save(block);
					status = new Status(false, 200, "updated");
					
				} else if (bl.getBlkId() == blockDto.getBlkId()) { 
					block = modelMapper.map(blockDto,Block.class);
					block.setBranch(branch);
					block.setBlkCreatedDate(new Date());
					block.setBlkIsActive(true);
					blockRepository.save(block);
					status = new Status(false, 200, "updated");
					
				}
				else{ 
					status = new Status(true,400,"Block name already exist");
					
				}
			}

			else {
				status = new Status(false, 400, "Branch not found");
				

			}
		}
		catch(Exception e) {
			status = new Status(true,500, "Oops..! Something went wrong..");
			
		}
		finally {
			displayLock.unlock();
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
					response.status = new Status(false,200, "deleted");

				}else  {
					block.setBlkIsActive(false);
					blockRepository.save(block);
					response.status = new Status(false,200, "The record has been disabled since it has been used in other transactions");
				}
			}else {
				response.status = new Status(true,400, "Block not found");
			}
		}catch(Exception e) { 
			response.status = new Status(true,500, "Oops..! Something went wrong..");
		}
		return response;
	}




	@Override
	public BlockResponse getBranchDetailsByCustId(Integer custId) {
		BlockResponse response=new BlockResponse(); 
		List<BlockDto> blockDtoList = new ArrayList<>();
		try {

			List<Object[]> blockList = blockRepository.findBranchDetailsByCustomer_CustId(custId);
			if(!blockList.isEmpty()) {
				for (Object[] p : blockList) {	
					BlockDto blockDto1 = new BlockDto();
					blockDto1.setCustId(Integer.valueOf(p[0].toString()));
					blockDto1.setRefBranchId(Integer.valueOf(p[1].toString()));
					blockDto1.setBrCode(p[2].toString());
					blockDto1.setBrName(p[3].toString());
					blockDtoList.add(blockDto1);
					response.setData1(blockDtoList);
					response.status = new Status(false,200, "Success");
				}
			}else {
				response.status = new Status(false,400, "Not found");
			}


		}catch (Exception e){
			response.status = new Status(true,500, "Oops..! Something went wrong..");


		}
		return response;
	}



	@Override
	public BlockResponse getBlockDetailsByCustIdANDRefBranchId(int custId,int refBranchId) { 
		BlockResponse response=new BlockResponse(); 
		List<BlockDto> blockDtoList = new ArrayList<>();
		try {
			List<Object[]> blockList = blockRepository.findBlockDetailsByCustomer_CustIdAndBranch_RefBranchId( custId, refBranchId);
			if(!blockList.isEmpty()) {
				for (Object[] p : blockList) {

					BlockDto blockDto1 = new BlockDto();
					blockDto1.setBlkId(Integer.valueOf(p[0].toString()));
					blockDto1.setBlkLogicalName(p[1].toString());
					blockDto1.setBlkGPSRadius(Integer.valueOf(p[2].toString()));
					blockDto1.setBlkLatitude((Double)p[3]);
					blockDto1.setBlkLongitude((Double)p[4]);
					blockDto1.setCustId(Integer.valueOf(p[5].toString()));
					blockDto1.setRefBranchId(Integer.valueOf(p[6].toString()));
					blockDtoList.add(blockDto1);
					response.setData1(blockDtoList);
					response.status = new Status(false,200, "success");

				}
			} else {
			
				response.status = new Status(false,400, "Not found");
			}
			
		}catch (Exception e){
			response.status = new Status(true,500,"Oops..! Something went wrong..");
		}
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

				response.status = new Status(false,200, "Success");

			}
			else {
				response.status = new Status(true, 4000, "Not found");
			}
		}catch(Exception e) {
			response.status = new Status(true,500, "Oops..! Something went wrong.."); 

		}
		return response;
	}

	/*
	 * @Override public BlockResponse getAllByCustId(Integer custId) { BlockResponse
	 * response=new BlockResponse(); List<BlockDto> blockDtoList = new
	 * ArrayList<>(); try { List<Object[]> blockList =
	 * blockRepository.findAllByCustomer_CustIdAndBlkIsActive(custId, true);
	 * 
	 * for (Object[] p : blockList) { BlockDto blockDto1 = new BlockDto();
	 * blockDto1.setBlkId(Integer.valueOf(p[0].toString()));
	 * blockDto1.setRefBranchId(Integer.valueOf(p[1].toString()));
	 * blockDto1.setCustId(Integer.valueOf(p[2].toString()));
	 * blockDto1.setBrName(p[3].toString());
	 * blockDto1.setBlkLogicalName(p[4].toString());
	 * blockDto1.setBlkGPSRadius(Integer.valueOf(p[5].toString()));
	 * blockDto1.setBlkLatLong(p[6].toString());
	 * blockDto1.setBlkCreatedDate((Date)p[7]);
	 * blockDto1.setBlkIsActive(Boolean.valueOf(p[8].toString()));
	 * blockDtoList.add(blockDto1); response.status = new Status(false,200,
	 * "Success");
	 * 
	 * }
	 * 
	 * 
	 * }catch (Exception e){ response.status = new Status(true,4000,e.getMessage());
	 * 
	 * 
	 * } response.setData1(blockDtoList); return response; }
	 */
	public BlockResponse getByCustomer_CustId(Integer custId) {
		BlockResponse response = new BlockResponse();

		try {
			Customer customer = customerRepository.findCustomerByCustId(custId);
			if(customer != null) {
				List<Block> blockList = blockRepository.findByCustomer_CustId(custId);
				response.setData1(blockList.stream().map(block -> convertToBlockDto(block)).collect(Collectors.toList()));

				if(response.getData1().isEmpty()) {
					response.status = new Status(false,400, "Not found"); 
				}else {
					response.status = new Status(false,200, "Success");

				}
			}else {
				response.status = new Status(true,400, "Not found for this customer"); 
			}

		}catch(Exception e) {
			response.status = new Status(true,500, "Oops..! Something went wrong.."); 
		}
		return response;
	}

	public BlockDto convertToBlockDto(Block block) {
		BlockDto blockDto = modelMapper.map(block,BlockDto.class);
		blockDto.setCustId(block.getBranch().getCustomer().getCustId());
		blockDto.setRefBranchId(block.getBranch().getBrId());
		blockDto.setBrName(block.getBranch().getBrName());
		blockDto.setBrCode(block.getBranch().getBrCode());
		return blockDto;
	}

	@Override
	public BlockResponse getBranchesByCustId(Integer custId) {
		BlockResponse response=new BlockResponse(); 
		List<BlockDto> blockDtoList = new ArrayList<>();
		try {

			List<Object[]> blockList = blockRepository.findBranchListByCustomer_CustId(custId);
			if(!blockList.isEmpty()) {
				for (Object[] p : blockList) {	
					BlockDto blockDto1 = new BlockDto();
					blockDto1.setCustId(Integer.valueOf(p[0].toString()));
					blockDto1.setRefBranchId(Integer.valueOf(p[1].toString()));
					blockDto1.setBrCode(p[2].toString());
					blockDto1.setBrName(p[3].toString());
					blockDtoList.add(blockDto1);
					response.setData1(blockDtoList);
					response.status = new Status(false,200, "Success");
				}
			}else {
				response.status = new Status(false,400, "Not found");
			}


		}catch (Exception e){
			response.status = new Status(true,500, "Oops..! Something went wrong..");


		}
		return response;
	}

}
