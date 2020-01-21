package com.lng.attendancecompanyservice.serviceImpl.masters;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecompanyservice.entity.custOnboarding.Customer;
import com.lng.attendancecompanyservice.entity.masters.IndustryType;
import com.lng.attendancecompanyservice.repositories.custOnboarding.CustomerRepository;
import com.lng.attendancecompanyservice.repositories.masters.IndustryTypeRepository;
import com.lng.attendancecompanyservice.service.masters.IndustryTypeService;
import com.lng.dto.masters.industryType.IndustryTypeDto;
import com.lng.dto.masters.industryType.IndustryTypeListResponse;
import com.lng.dto.masters.industryType.IndustryTypeResponse;

import status.Status;
import status.StatusDto;

@Service
public class IndustryTypeServiceImpl implements IndustryTypeService {

	@Autowired
	IndustryTypeRepository industryTypeRepository;

	@Autowired
	CustomerRepository customerRepository;

	ModelMapper modelMapper = new ModelMapper();
	
	private final Lock displayQueueLock = new ReentrantLock();

	@Override
	public StatusDto saveIndustryType(IndustryTypeDto industryTypeDto) {
		final Lock displayLock = this.displayQueueLock; 
		StatusDto statusDto = new StatusDto();
		// Check if industry type already exist or not
		IndustryType industryType = industryTypeRepository.findIndustryTypeByIndustryName(industryTypeDto.getIndustryName());
		
		IndustryType industryType1 = industryTypeRepository.findIndustryTypeByIndustryNameAndIndustryIsActive(industryTypeDto.getIndustryName(), false);
		
		try {
			displayLock.lock();
			if(industryType == null) {

				industryType = modelMapper.map(industryTypeDto, IndustryType.class);
				industryType.setIndustryIsActive(true);
				industryTypeRepository.save(industryType);
				statusDto.setCode(200);
				statusDto.setError(false);
				statusDto.setMessage("created");
				
			}else if(industryType1 != null){
				
				industryType1 = modelMapper.map(industryTypeDto, IndustryType.class);
				industryType1.setIndustryIsActive(true);
				industryTypeRepository.save(industryType1);
				statusDto.setCode(200);
				statusDto.setError(false);
				statusDto.setMessage("created");
				
			}else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("Industry type already exist");
				
			}
		}catch (Exception e) {
			statusDto.setCode(500);
			statusDto.setError(true);
			statusDto.setMessage("Oops..! Something went wrong..");
			
		}
		finally {
			displayLock.unlock();
		}
		return statusDto;
	}

	@Override
	public IndustryTypeListResponse findAllIndustryTypeByIndustryIsActive() {

		IndustryTypeListResponse industryTypeListResponse = new IndustryTypeListResponse();
		try {
			List<IndustryType> industryTypeDtoList =  industryTypeRepository.findAllByIndustryIsActive();

			industryTypeListResponse.setIndustryTypeDtoList(industryTypeDtoList.stream().map(industryType -> convertToIndustryTypeDto(industryType)).collect(Collectors.toList()));

			if(industryTypeListResponse != null && industryTypeListResponse.getIndustryTypeDtoList() != null) {

				industryTypeListResponse.status = new Status(false, 200, "Success");
			}else {
				industryTypeListResponse.status = new Status(false, 400, "Not found");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			industryTypeListResponse.status = new Status(true,500, "Oops..! Something went wrong..");
		}
		return industryTypeListResponse;
	}	
	@Override
	public IndustryTypeListResponse findAllIndustryType() {

		IndustryTypeListResponse industryTypeListResponse = new IndustryTypeListResponse();
		try {
			List<IndustryType> industryTypeDtoList =  industryTypeRepository.findAll();

			industryTypeListResponse.setIndustryTypeDtoList(industryTypeDtoList.stream().map(industryType -> convertToIndustryTypeDto(industryType)).collect(Collectors.toList()));

			if(industryTypeListResponse != null && industryTypeListResponse.getIndustryTypeDtoList() != null) {

				industryTypeListResponse.status = new Status(false, 200, "Success");
			}else {
				industryTypeListResponse.status = new Status(false, 400, "Not found");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			industryTypeListResponse.status = new Status(true,500, "Oops..! Something went wrong..");
		}
		return industryTypeListResponse;
	}	
	@Override
	public IndustryTypeResponse findIndustryByIndustryid(Integer industryId) {
		IndustryTypeResponse industryTypeResponse = new IndustryTypeResponse();
		try {
			IndustryType industryType = industryTypeRepository.findIndustryTypeByIndustryId(industryId);
			if(industryType != null) {
				IndustryTypeDto industryTypeDto = convertToIndustryTypeDto(industryType);
				industryTypeResponse.data = industryTypeDto;
				industryTypeResponse.status = new Status(false, 200, "Success");
			}else {
				industryTypeResponse.status = new Status(false, 400, "Not found");
			}
		}catch (Exception e) {
			industryTypeResponse.status = new Status(true,500, "Oops..! Something went wrong..");
		}
		return industryTypeResponse;
	}


	@Override
	public StatusDto updateIndustryType(IndustryTypeDto industryTypeDto) {
		final Lock displayLock = this.displayQueueLock; 
		StatusDto statusDto = new StatusDto();

		IndustryType industryType1 = industryTypeRepository.findIndustryTypeByIndustryId(industryTypeDto.getIndustryId());
		IndustryType industryType2 = industryTypeRepository.findIndustryTypeByIndustryName(industryTypeDto.getIndustryName());
		try {
			displayLock.lock();
			if(industryType1 != null) {
				if(industryType2 == null || (industryTypeDto.getIndustryId() == industryType1.getIndustryId() && industryTypeDto.getIndustryName().equals(industryType1.getIndustryName()))) {
					industryType1.setIndustryName(industryTypeDto.getIndustryName());
					industryType1.setIndustryIsActive(true);
					industryTypeRepository.save(industryType1);
					statusDto.setCode(200);
					statusDto.setError(false);
					statusDto.setMessage("updated");
					
				}else {
					statusDto.setCode(400);
					statusDto.setError(true);
					statusDto.setMessage("Industry already exist");
					
				}
			}else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("Industry not found");
				
			}

		}catch (Exception e) {
			statusDto.setCode(500);
			statusDto.setError(true);
			statusDto.setMessage("Oops..! Something went wrong..");
			
		}
		finally {
			displayLock.unlock();
		}

		return statusDto;
	}

	@Override
	public StatusDto deleteIndustryByIndustryId(Integer industryId) {
		StatusDto statusDto = new StatusDto();

		IndustryType industryType = industryTypeRepository.findIndustryTypeByIndustryId(industryId);
		List<Customer> customer = customerRepository.findCustomerByIndustryType_IndustryId(industryId);
		try {
			if(industryType != null) {
			if(customer.isEmpty()) {
			
					industryTypeRepository.delete(industryType);
					statusDto.setCode(200);
					statusDto.setError(false);
					statusDto.setMessage("deleted");
				
				}else {
					industryType.setIndustryIsActive(false);
					industryTypeRepository.save(industryType);
					statusDto.setCode(200);
					statusDto.setError(false);
					statusDto.setMessage("The record has been disabled since it has been used in other transactions");
				}
				
			}else {
				statusDto.setCode(400);
				statusDto.setError(true);
				statusDto.setMessage("Industry not found");
				
			}
		}catch (Exception e) {
			statusDto.setCode(500);
			statusDto.setError(true);
			statusDto.setMessage("Oops..! Something went wrong..");		
		}

		return statusDto;
	}

	public IndustryTypeDto convertToIndustryTypeDto(IndustryType industryType) {
		IndustryTypeDto  industryTypeDto = modelMapper.map(industryType, IndustryTypeDto.class);
		return industryTypeDto;
	}

}
