package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
	
	private final Lock displayQueueLock = new ReentrantLock();
	
	@Override
	public ContractorResponse saveContractor(ContractorDto contractorDto) {
		final Lock displayLock = this.displayQueueLock; 
		ContractorResponse response = new ContractorResponse();
		try{
			displayLock.lock();
			if(contractorDto.getContractorName() == null || contractorDto.getContractorName().isEmpty()) throw new Exception("Please enter Contractor name");


			int a = contractorRepository.findByRefCustIdAndContractorName(contractorDto.getRefCustId(), contractorDto.getContractorName());
			Contractor contractor1 = contractorRepository.findByCustomer_CustIdAndContractorNameAndContractorIsActive(contractorDto.getRefCustId(), contractorDto.getContractorName(), false);

			if(a == 0) {
				Customer customer = customerRepository.findCustomerByCustIdAndCustIsActive(contractorDto.getRefCustId(), true);
				if(customer != null) {

					Contractor contractor = new Contractor();
					contractor.setCustomer(customer);
					contractor.setContractorName(contractorDto.getContractorName());
					contractor.setContractorIsActive(true);
					contractorRepository.save(contractor);
					response.status = new Status(false,200, "created");
					
				}
				else{ 
					response.status = new Status(true,400, "Customer Not Found");
					
				}
			} else if(contractor1 != null){
				Customer customer = customerRepository.findCustomerByCustIdAndCustIsActive(contractorDto.getRefCustId(), true);
				if(customer != null) {
					
					contractor1.setCustomer(customer);
					contractor1.setContractorName(contractorDto.getContractorName());
					contractor1.setContractorIsActive(true);
					contractorRepository.save(contractor1);
					response.status = new Status(false,200, "created");
					

				}
				else{ 
					response.status = new Status(true,400, "Customer Not Found");
					
				}

			} else {
				response.status = new Status(true,400,"Contractor name already exists");
				
			}

		}catch(Exception ex){
			response.status = new Status(true,500, ex.getMessage()); 
			
		}
		finally {
			displayLock.unlock();
		}
		return response;
	}

	@Override
	public ContractorResponse getAll() {
		ContractorResponse response = new ContractorResponse();
		try {
			List<Contractor> contractorList=contractorRepository.findAllByContractorIsActive(true);
			response.setData1(contractorList.stream().map(contractor -> convertToContractorDto(contractor)).collect(Collectors.toList()));
			response.status = new Status(false,200, "Success");
		}catch(Exception e) {
			response.status = new Status(true,500, e.getMessage()); 

		}
		return response;
	}

	@Override
	public Status updateContractorByContractorId(ContractorDto contractorDto) {
		final Lock displayLock = this.displayQueueLock;
		Status status = null;
		try {
			displayLock.lock();
			if(contractorDto.getContractorName() == null || contractorDto.getContractorName().isEmpty()) throw new Exception("Please enter Contractor name");
			if(contractorDto.getContractorId() == null || contractorDto.getContractorId() == 0) throw new Exception("Contractor id is null or zero");

			Contractor contractor = contractorRepository.findContractorByContractorId(contractorDto.getContractorId())	;	

			Customer customer = customerRepository.findCustomerByCustIdAndCustIsActive(contractorDto.getRefCustId(), true);
			if(customer != null) {
				Contractor ch = contractorRepository.findContractorBycontractorNameAndCustomer_custId(contractorDto.getContractorName(), contractorDto.getRefCustId());
				if(ch == null) {

					contractor = modelMapper.map(contractorDto,Contractor.class);
					contractor.setCustomer(customer);
					contractor.setContractorIsActive(true);
					contractorRepository.save(contractor);
					status = new Status(false, 200, "updated");
					
				} else if (ch.getContractorId() == contractorDto.getContractorId()) { 

					contractor = modelMapper.map(contractorDto,Contractor.class);
					contractor.setCustomer(customer);
					contractor.setContractorIsActive(true);
					contractorRepository.save(contractor);
					status = new Status(false, 200, "updated");
					
				}
				else{
					status = new Status(true,400,"Contractor name already exists");
					
				}
			}

			else {
				status = new Status(false, 400, "Customer Not Found");
				
			}
		}
		catch(Exception e) {
			status = new Status(true,500, "Oops..! Something went wrong");
			
		}
		finally {
			displayLock.unlock();
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
					scontractorResponse.status = new Status(false,200, "deleted");
				}else {
					contractor.setContractorIsActive(false);
					contractorRepository.save(contractor);
					scontractorResponse.status = new Status(false,200, "The record has been disabled since it has been used in other transactions");
				}

			} else {
				scontractorResponse.status = new Status(true,400, "contractorId Not Found");
			}

		}catch(Exception e) { 
			scontractorResponse.status = new Status(true,500, "Oops..! Something went wrong");
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
				response.status = new Status(false,200, "Success");
			}
			else {
				response.status = new Status(true, 400, "Not found");
			}
		}catch(Exception e) {
			response.status = new Status(true,500, "Oops..! Something went wrong"); 

		}
		return response;
	}

	@Override
	public ContractorResponse getAllByCustId(Integer custId) {
		ContractorResponse response = new ContractorResponse();
		try {
			List<Contractor> contractorList = contractorRepository.findAllByCustomer_CustId(custId);
			response.setData1(contractorList.stream().map(contractor -> convertToContractorDto(contractor)).collect(Collectors.toList()));

			if(response.getData1().isEmpty()) {
				response.status = new Status(false,400, "Not found"); 
			}else {
				response.status = new Status(false,200, "Success");
			}

		}catch(Exception e) {
			response.status = new Status(true,500, "Oops..! Something went wrong"); 
		}
		return response;
	}
}

