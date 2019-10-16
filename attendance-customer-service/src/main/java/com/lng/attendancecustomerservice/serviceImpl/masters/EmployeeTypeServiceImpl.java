package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.EmployeeType;
import com.lng.attendancecustomerservice.repositories.masters.EmployeeTypeRepository;
import com.lng.attendancecustomerservice.service.masters.EmployeeTypeService;
import com.lng.dto.employeeType.EmployeeTypeDto;
import com.lng.dto.employeeType.EmployeeTypeListResponseDto;
import com.lng.dto.employeeType.StatusDto;

import status.Status;

@Service
public class EmployeeTypeServiceImpl implements EmployeeTypeService {

	@Autowired
	EmployeeTypeRepository employeeTypeRepository;

	ModelMapper modelMapper = new ModelMapper();

	@Override
	public StatusDto save(EmployeeTypeDto employeeTypeDto) {

		StatusDto statusDto = new StatusDto();

		// Check employee type already exist or no
		 EmployeeType employeeType = employeeTypeRepository.findEmployeeTypeByEmpType(employeeTypeDto.getEmpType());		 
		 
		try {
			if(employeeType == null) {
				employeeType = modelMapper.map(employeeTypeDto, EmployeeType.class);
				employeeTypeRepository.save(employeeType);
				statusDto.setCode(200);
				statusDto.setError(false);
				statusDto.setMessage("Successfully Saved");
			}else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("Employee Type Already Exist");
			}
		}catch (Exception e) {
			statusDto.setCode(500);
			statusDto.setError(true);
			statusDto.setMessage("Something Went Wrong");
		}

		return statusDto;

	}

	@Override
	public EmployeeTypeListResponseDto findAll() {
		
		EmployeeTypeListResponseDto employeeTypeListResponseDto = new EmployeeTypeListResponseDto();
		
		try {
			List<EmployeeType> employeeTypeDtoList =  employeeTypeRepository.findAll();

			employeeTypeListResponseDto.setEmployeeTypeDtoList(employeeTypeDtoList.stream().map(employeeType -> convertToEmployeeTypeDto(employeeType)).collect(Collectors.toList()));

			if(employeeTypeListResponseDto != null && employeeTypeListResponseDto.getEmployeeTypeDtoList() != null) {
				
				employeeTypeListResponseDto.status = new Status(false, 2000, "Success");
			}else {
				employeeTypeListResponseDto.status = new Status(true, 4000, "Not Found");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			employeeTypeListResponseDto.status = new Status(true, 5000, "Something went wrong");
		}
		return employeeTypeListResponseDto;
	}
		
	@Override
	public StatusDto updateEmpType(EmployeeTypeDto employeeTypeDto) {
		StatusDto statusDto = new StatusDto();
		
		EmployeeType employeeType = employeeTypeRepository.findEmployeeTypeByEmpTypeId(employeeTypeDto.getEmpTypeId());
		
		//EmployeeType employeeType = employeeTypeRepository.findEmployeeTypeByEmpTypeIdAndEmpType(employeeTypeDto.getEmpTypeId(), employeeTypeDto.getEmpType());
		
		try {
			if(employeeType != null) {
				if(employeeTypeDto.getEmpTypeId() != 0 && employeeTypeDto.getEmpType() != null) {
					employeeType.setEmpType(employeeTypeDto.getEmpType());
					employeeTypeRepository.save(employeeType);
					statusDto.setCode(200);
					statusDto.setError(false);
					statusDto.setMessage("Successfully Updated");
				}else {
					statusDto.setCode(400);
					statusDto.setError(true);
					statusDto.setMessage("Please enter Employee Type Id and Employee Type Name");
				}
				
			}else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("Cannot Update");
			}
				
		}catch (Exception e) {
			statusDto.setCode(500);
			statusDto.setError(true);
			statusDto.setMessage("Something Went Wrong");
		}
		return statusDto;
	}
	
	@Override
	public StatusDto deleteEmpTypeByEmpTypeId(Integer empTypeId) {
		
		return null;
	}
	
	public EmployeeTypeDto convertToEmployeeTypeDto(EmployeeType employeeType) {
		EmployeeTypeDto  employeeTypeDto = modelMapper.map(employeeType, EmployeeTypeDto.class);
        return employeeTypeDto;
    }	
}
