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
					contractorRepository.save(contractor);
					response.status = new Status(false,200, "successfully created");

				}
				else{ 
					response.status = new Status(true,400, "CustomerId Not Found");
				}
			}
			else{ 
				response.status = new Status(true,400,"ContractorName already exist for CustomerId :" + contractorDto.getRefCustId());
			}

		}catch(Exception ex){
			response.status = new Status(true,3000, ex.getMessage()); 
		}

		return response;
	}


	/*
	 * private Boolean CheckContractorExists(String contractorName) { Contractor
	 * contractor = contractorRepository.findByContractorName(contractorName);
	 * if(contractor != null) { return true; } else { return false; } }
	 */

	@Override
	public ContractorResponse getAll() {
		ContractorResponse response = new ContractorResponse();
		try {
			List<Contractor> contractorList=contractorRepository.findAll();
			response.setData(contractorList.stream().map(contractor -> convertToContractorDto(contractor)).collect(Collectors.toList()));
			response.status = new Status(false,200, "successfully  GetAll");
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

			Contractor 	contractor = contractorRepository.findContractorByContractorId(contractorDto.getContractorId())	;		

			Customer customer = customerRepository.findCustomerByCustId(contractorDto.getRefCustId());
			if(customer != null) {
				Contractor ch = contractorRepository.findContractorBycontractorNameAndCustomer_custId(contractorDto.getContractorName(), contractorDto.getRefCustId());
				if(ch == null) {

					contractor = modelMapper.map(contractorDto,Contractor.class);
					contractor.setCustomer(customer);
					contractorRepository.save(contractor);
					status = new Status(false, 200, "Updated successfully");
				} else if (ch.getContractorId() == contractorDto.getContractorId()) { 

					contractor = modelMapper.map(contractorDto,Contractor.class);
					contractor.setCustomer(customer);
					contractorRepository.save(contractor);
					status = new Status(false, 200, "Updated successfully");
				}
				else{ 
					status = new Status(true,400,"ContractorName already exist for CustomerId :" + contractorDto.getRefCustId());
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

			int a = contractorRepository.findEmployeeByContractorContractorId(contractorId);
			if(a == 0) {
				Contractor contractor = contractorRepository.findContractorByContractorId(contractorId);
				if(contractor!= null) {
					contractorRepository.delete(contractor);					
					scontractorResponse.status = new Status(false,200, "successfully deleted");
				}

			} else  {
				scontractorResponse.status = new Status(true,400, "Cannot Delete");
			}

		}catch(Exception e) { 
			scontractorResponse.status = new Status(true,400, "contractorId Not Found");
		}

		return scontractorResponse;
	}


}
