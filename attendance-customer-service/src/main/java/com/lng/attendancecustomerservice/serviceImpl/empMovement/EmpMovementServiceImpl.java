package com.lng.attendancecustomerservice.serviceImpl.empMovement;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.empMovement.EmpMovement;
import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.repositories.empAppSetup.EmployeeRepository;
import com.lng.attendancecustomerservice.repositories.empMovement.EmpMovementRepository;
import com.lng.attendancecustomerservice.service.empMovement.EmpMovementService;
import com.lng.dto.empMovement.EmpMovementDto;
import com.lng.dto.empMovement.EmpMovementResponse;

import status.Status;
@Service
public class EmpMovementServiceImpl implements EmpMovementService {
	
	ModelMapper modelMapper=new ModelMapper();

	@Autowired
	EmployeeRepository employeeRepository;
	
	@Autowired
	EmpMovementRepository empMovementRepository;

	@Override
	public EmpMovementResponse save(EmpMovementDto empMovementDto) {
		EmpMovementResponse  empMovementResponse = new EmpMovementResponse();
		EmpMovement empMovement = new EmpMovement();
		try {
			Employee  employee = employeeRepository.getByEmpId(empMovementDto.getRefEmpId());
			if(employee != null) {
				empMovement.setEmployee(employee);
				empMovement.setEmpMovementDate(empMovementDto.getEmpMovementDate());
				empMovement.setEmpMovementDatetime(empMovementDto.getEmpMovementDatetime());
				empMovement.setEmpMovementLatLong(empMovementDto.getEmpMovementLatLong());
				empMovement.setEmpMovementMode(empMovementDto.getEmpMovementMode());
				empMovement.setEmpMovementType(empMovementDto.getEmpMovementType());
				empMovementRepository.save(empMovement);
				empMovementResponse.status = new Status(false,200, "successfully created");
				
			}
			else{ 
				empMovementResponse.status = new Status(true,400, "Employee not found");
			}

		}catch(Exception e){
			empMovementResponse.status = new Status(true,500,"Oops..! Something went wrong");

		}

		return empMovementResponse;
	}

	@Override
	public EmpMovementResponse getAll(Integer refEmpId,Date empMovementDate) {
		EmpMovementResponse  empMovementResponse = new EmpMovementResponse();
		try {
			List<EmpMovement> empMovementList = empMovementRepository.findEmpMovementByEmployee_EmpIdAndEmpMovementDate(refEmpId, empMovementDate);
			if(!empMovementList.isEmpty()) {
			empMovementResponse.setData1(empMovementList.stream().map(empMovement -> convertToEmpMovementDto(empMovement)).collect(Collectors.toList()));
			empMovementResponse.status = new Status(false,200, "Success");
			}else {
				empMovementResponse.status = new Status(false,400, "Not found");
			}
		}catch(Exception e) {
			empMovementResponse.status = new Status(true,500, "Oops..! Something went wrong"); 

		}
		return empMovementResponse;
	}
	
	public EmpMovementDto convertToEmpMovementDto(EmpMovement empMovement) {
		EmpMovementDto empMovementDto = modelMapper.map(empMovement,EmpMovementDto.class); 
		empMovementDto.setRefEmpId(empMovement.getEmployee().getEmpId());
		empMovementDto.setEmpName(empMovement.getEmployee().getEmpName());
		return empMovementDto;
	}

}
