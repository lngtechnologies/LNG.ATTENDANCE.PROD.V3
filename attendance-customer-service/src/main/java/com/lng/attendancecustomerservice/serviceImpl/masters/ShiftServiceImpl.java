package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.Branch;
import com.lng.attendancecustomerservice.entity.masters.Shift;
import com.lng.attendancecustomerservice.repositories.empAppSetup.EmployeeRepository;
import com.lng.attendancecustomerservice.repositories.masters.BranchRepository;
import com.lng.attendancecustomerservice.repositories.masters.ShiftRepository;
import com.lng.attendancecustomerservice.service.masters.ShiftService;
import com.lng.dto.customer.BranchDto;
import com.lng.dto.masters.shift.ShiftDto;
import com.lng.dto.masters.shift.ShiftResponse;

import status.Status;
@Service
public class ShiftServiceImpl implements ShiftService {

	ModelMapper modelMapper=new ModelMapper();

	@Autowired
	ShiftRepository shiftRepository;
	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	BranchRepository branchRepository;



	@Override
	public ShiftResponse saveShift(ShiftDto shiftDto) { 

		ShiftResponse shiftResponse = new ShiftResponse();

		Shift shift = new Shift();
		try {
			if(shiftDto.getShiftName() == null || shiftDto.getShiftName().isEmpty())  throw new Exception("Plz Enter Shift Name");

			int a = shiftRepository.findByRefBrIdAndShiftName(shiftDto.getRefBrId(), shiftDto.getShiftName());
			if(a == 0) {
				Branch branch = branchRepository.findBranchByBrId(shiftDto.getRefBrId());
				if(branch != null) {
					shift.setBranch(branch);
					shift.setShiftName(shiftDto.getShiftName());
					shift.setShiftStart(shiftDto.getShiftStart());
					shift.setShiftEnd(shiftDto.getShiftEnd());
					shiftRepository.save(shift);
					shiftResponse.status = new Status(false,200, "successfully created");

				}
				else{ 
					shiftResponse.status = new Status(true,400, "BranchId Not Found");
				}
			}
			else{ 
				shiftResponse.status = new Status(true,400,"ShiftName already exist for branch :" + shiftDto.getRefBrId());
			}
		} catch (Exception e) {
			shiftResponse.status = new Status(true, 4000, e.getMessage());
		}

		return shiftResponse;
	}


	/*
	 * private Boolean CheckShiftExists(String shiftName) { Shift shift =
	 * shiftRepository.findByShiftName(shiftName); if(shift != null) { return true;
	 * } else { return false; } }
	 */

	@Override
	public ShiftResponse getAll() {
		ShiftResponse response = new ShiftResponse();
		try {
			List<Shift> shiftList=shiftRepository.findAll();
			response.setData(shiftList.stream().map(shift -> convertToShiftDto(shift)).collect(Collectors.toList()));
			response.status = new Status(false,200, "successfully  GetAll");
		}catch(Exception e) {
			response.status = new Status(true,3000, e.getMessage()); 

		}
		return response;
	}
	@Override
	public Status updateShiftByShiftId(ShiftDto shiftDto) {
		Status status = null;
		try {
			if(shiftDto.getShiftName() == null || shiftDto.getShiftName().isEmpty()) throw new Exception("Please enter Shift name");
			if(shiftDto.getShiftId() == null || shiftDto.getShiftId() == 0) throw new Exception("Shift id is null or zero");
			if(shiftDto.getRefBrId() == null || shiftDto.getRefBrId() == 0) throw new Exception("RefBranchId id is null or zero");
			Shift 	shift = shiftRepository.findShiftByShiftId(shiftDto.getShiftId());
			Branch branch = branchRepository.findBranchByBrId(shiftDto.getRefBrId());
			if(branch != null) {
			int b = shiftRepository.findByRefBrIdAndShiftName(shiftDto.getRefBrId(), shiftDto.getShiftName());
			   if(b == 0) {
			//if(CheckShiftExists(shiftDto.getShiftName())) throw new Exception("Shift already exists");


			shift = modelMapper.map(shiftDto,Shift.class);
			shift.setBranch(branch);
			shiftRepository.save(shift);
			status = new Status(false, 200, "Updated successfully");
			   }
		         else{ 
		 		status = new Status(true,400,"ShiftName already exist for Customer :" + shiftDto.getRefBrId());
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
	public ShiftResponse deleteByShiftId(Integer shiftId) {
		ShiftResponse shiftResponse=new ShiftResponse(); 
		try {

			int b = shiftRepository.findEmployeeByShiftShiftId(shiftId);
			if(b == 0) {
				Shift shift = shiftRepository.findShiftByShiftId(shiftId);
				if(shift!= null) {
					shiftRepository.delete(shift);					
					shiftResponse.status = new Status(false,200, "successfully deleted");
				}

			} else  {
				shiftResponse.status = new Status(true,400, "Cannot Delete");
			}

		}catch(Exception e) { 
			shiftResponse.status = new Status(true,400, "ShiftId Not Found");
		}

		return shiftResponse;
	}

	public ShiftDto convertToShiftDto(Shift shift) {
		ShiftDto shiftDto = modelMapper.map(shift,ShiftDto.class);
		shiftDto.setRefBrId(shift.getBranch().getBrId());
		shiftDto.setBrName(shift.getBranch().getBrName());
		BranchDto branchDto = modelMapper.map(shift.getBranch(),BranchDto.class);
		return shiftDto;
	}


	@Override
	public ShiftResponse getBlockDetailsByRefBrId(Integer refBrId) {
		//ShiftDto shiftDto = new ShiftDto();
		ShiftResponse response = new ShiftResponse();
		List<ShiftDto> shiftDtoList = new ArrayList<>();
		try {

			List<Object[]> shiftList = shiftRepository.findShiftDetailsByBranch_RefBrId(refBrId);

			for (Object[] p : shiftList) {	
				ShiftDto shiftDto1 = new ShiftDto();
				shiftDto1.setShiftName(p[0].toString());
				shiftDto1.setShiftStart((p[1].toString()));
				shiftDto1.setShiftEnd((p[2].toString()));
				shiftDto1.setRefBrId(Integer.valueOf(p[3].toString()));
				shiftDtoList.add(shiftDto1);
				response.status = new Status(false,200, "successfully GetShiftDetails");

			}


		}catch (Exception e){
			response.status = new Status(true,4000, e.getMessage());


		}
		response.setData(shiftDtoList);
		response.status = new Status(false,4000, "BranchId not Found");
		return response;
	}




}
