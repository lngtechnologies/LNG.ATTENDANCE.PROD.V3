package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.Contractor;
import com.lng.attendancecustomerservice.entity.masters.Customer;
import com.lng.attendancecustomerservice.repositories.empAppSetup.EmployeeRepository;
import com.lng.attendancecustomerservice.repositories.masters.ContractorRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustomerRepository;
import com.lng.attendancecustomerservice.service.masters.ContractorService;
import com.lng.dto.masters.contractor.ContractorDto;
import com.lng.dto.masters.contractor.ContractorResponse;

import status.Status;
@Service
public class ContractorServiceImpl implements ContractorService {

	ModelMapper modelMapper=new ModelMapper();
	@Autowired
	ContractorRepository contractorRepository;
	@Autowired
	EmployeeRepository employeeRepository;
	@Autowired
	CustomerRepository customerRepository;
	@Override

	public ContractorResponse saveContractor(ContractorDto contractorDto) {
		ContractorResponse response = new ContractorResponse();
		try{
			if(contractorDto.getContractorName() == null || contractorDto.getContractorName().isEmpty()) throw new Exception("Please enter Contractor name");


			int a = contractorRepository.findByRefCustIdAndContractorName(contractorDto.getRefCustId(), contractorDto.getContractorName());

			if(a == 0) {
				Customer customer = customerRepository.findCustomerByCustId(contractorDto.getRefCustId());
				if(customer != null) {

					Contractor contractor = new Contractor();
					contractor.setCustomer(customer);
					contractor.setContractorName(contractorDto.getContractorName());
					contractor.setContractorIsActive(true);
					contractorRepository.save(contractor);
					response.status = new Status(false,200, "successfully created");

				}
				else{ 
					response.status = new Status(true,400, "CustomerId Not Found");
				}
			}
			else{ 
				response.status = new Status(true,400,"ContractorName already exist for Customer");
			}

		}catch(Exception ex){
			response.status = new Status(true,3000, ex.getMessage()); 
		}

		return response;
	}

	@Override
	public ContractorResponse getAll() {
		ContractorResponse response = new ContractorResponse();
		try {
			List<Contractor> contractorList=contractorRepository.findAllByContractorIsActive(true);
			response.setData1(contractorList.stream().map(contractor -> convertToContractorDto(contractor)).collect(Collectors.toList()));
			response.status = new Status(false,200, "successfully GetAll");
		}catch(Exception e) {
			response.status = new Status(true,3000, e.getMessage()); 

		}
		return response;
	}

	@Override
	public Status updateContractorByContractorId(ContractorDto contractorDto) {
		Status status = null;
		try {
			if(contractorDto.getContractorName() == null || contractorDto.getContractorName().isEmpty()) throw new Exception("Please enter Contractor name");
			if(contractorDto.getContractorId() == null || contractorDto.getContractorId() == 0) throw new Exception("Contractor id is null or zero");

			Contractor contractor = contractorRepository.findContractorByContractorId(contractorDto.getContractorId())	;	

			Customer customer = customerRepository.findCustomerByCustId(contractorDto.getRefCustId());
			if(customer != null) {
				Contractor ch = contractorRepository.findContractorBycontractorNameAndCustomer_custId(contractorDto.getContractorName(), contractorDto.getRefCustId());
				if(ch == null) {

					contractor = modelMapper.map(contractorDto,Contractor.class);
					contractor.setCustomer(customer);
					contractor.setContractorIsActive(true);
					contractorRepository.save(contractor);
					status = new Status(false, 200, "Updated successfully");
				} else if (ch.getContractorId() == contractorDto.getContractorId()) { 

					contractor = modelMapper.map(contractorDto,Contractor.class);
					contractor.setCustomer(customer);
					contractor.setContractorIsActive(true);
					contractorRepository.save(contractor);
					status = new Status(false, 200, "Updated successfully");
				}
				else{ 
					status = new Status(true,400,"ContractorName already exist for CustomerId");
				}
			}

			else {
				status = new Status(false, 200, "CustomerId Not Found");

			}
		}
		catch(Exception e) {
			status = new Status(true, 4000, e.getMessage());
		}
		return status;
	}

	public ContractorDto convertToContractorDto(Contractor contractor) {
		ContractorDto contractorDto = modelMapper.map(contractor,ContractorDto.class); 
		contractorDto.setContractorId(contractor.getContractorId());
		contractorDto.setRefCustId(contractor.getCustomer().getCustId());
		contractorDto.setCustName(contractor.getCustomer().getCustName());
		return contractorDto;
	}


	@Override
	public ContractorResponse deleteByContractorId(Integer contractorId) {
		ContractorResponse scontractorResponse=new ContractorResponse();
		try {
			Contractor contractor = contractorRepository.findContractorByContractorId(contractorId);
			int a = contractorRepository.findEmployeeByContractorContractorId(contractorId);
			if(contractor!= null) {
				if(a == 0) {
					contractorRepository.delete(contractor);	
					scontractorResponse.status = new Status(false,200, "successfully deleted");
				}else {
					contractor.setContractorIsActive(false);
					contractorRepository.save(contractor);
					scontractorResponse.status = new Status(false,200, "The record has been just disabled as it is already used");
				}

			} else {
				scontractorResponse.status = new Status(true,400, "contractorId Not Found");
			}

		}catch(Exception e) { 
			scontractorResponse.status = new Status(true,400, e.getMessage());
		}

		return scontractorResponse;
	}

	@Override
	public ContractorResponse getContractorByContractorId(Integer contractorId) {
		ContractorResponse response=new ContractorResponse();
		try {
			Contractor contractor = contractorRepository.findContractorByContractorId(contractorId);
			if(contractor != null) {
				ContractorDto contractorDto = convertToContractorDto(contractor);
				response.data = contractorDto;
				response.status = new Status(false,200, "successfully GetContractorDetails");
			}
			else {
				response.status = new Status(true, 4000, "Not found");
			}
		}catch(Exception e) {
			response.status = new Status(true,3000, e.getMessage()); 

		}
		return response;
	}

	@Override
	public ContractorResponse getAllByCustId(Integer custId) {
		ContractorResponse response = new ContractorResponse();
		try {
			List<Contractor> contractorList = contractorRepository.findAllByCustomer_CustIdAndContractorIsActive(custId, true);
			response.setData1(contractorList.stream().map(contractor -> convertToContractorDto(contractor)).collect(Collectors.toList()));
			
			if(response.getData1().isEmpty()) {
				response.status = new Status(true,400, "Contractor Not found"); 
			}else {
				response.status = new Status(false,200, "success");
			}

		}catch(Exception e) {
			response.status = new Status(true,500, "Something went wrong"); 
		}
		return response;
	}
}

