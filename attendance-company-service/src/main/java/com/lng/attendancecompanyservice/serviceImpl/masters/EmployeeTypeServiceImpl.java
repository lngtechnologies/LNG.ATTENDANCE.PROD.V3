package com.lng.attendancecompanyservice.serviceImpl.masters;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.masters.EmployeeType;
import com.lng.attendancecompanyservice.repositories.masters.EmployeeTypeRepository;
import com.lng.attendancecompanyservice.service.masters.EmployeeTypeService;
import com.lng.dto.masters.employeeType.EmployeeTypeDto;
import com.lng.dto.masters.employeeType.EmployeeTypeListResponseDto;

import status.Status;

@Service
public class EmployeeTypeServiceImpl implements EmployeeTypeService {
	
	ModelMapper modelMapper = new ModelMapper();
	
	@Autowired
	EmployeeTypeRepository employeeTypeRepository;

	@Override
	public EmployeeTypeListResponseDto findAll() {
		EmployeeTypeListResponseDto employeeTypeListResponseDto = new EmployeeTypeListResponseDto();
		try {

			List<EmployeeType> employeeTypeList = employeeTypeRepository.findAll();
			employeeTypeListResponseDto.setEmployeeTypeDtoList(employeeTypeList.stream().map(employeeType -> convertToEmployeeTypeDto(employeeType)).collect(Collectors.toList()));

			if(employeeTypeListResponseDto != null && employeeTypeListResponseDto.getEmployeeTypeDtoList() != null) {
				employeeTypeListResponseDto.status = new Status(false, 200, "Success");
			}else {
				employeeTypeListResponseDto.status = new Status(false, 400, "Not found");
			}
		} catch (Exception e) {
			employeeTypeListResponseDto.status = new Status(true,500, "Oops..! Something went wrong..");
		}
		return employeeTypeListResponseDto;
	
	}

	@Override
	public EmployeeTypeDto findById(Integer empTypeId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public EmployeeTypeDto convertToEmployeeTypeDto(EmployeeType employeeType) {
		EmployeeTypeDto employeeTypeDto = modelMapper.map(employeeType, EmployeeTypeDto.class);	
		return employeeTypeDto;
	}

}
