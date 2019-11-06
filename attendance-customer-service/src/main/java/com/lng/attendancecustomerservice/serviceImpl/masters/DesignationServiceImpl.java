package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.Customer;
import com.lng.attendancecustomerservice.entity.masters.Designation;
import com.lng.attendancecustomerservice.repositories.masters.CustomerRepository;
import com.lng.attendancecustomerservice.repositories.masters.DesignationRepository;
import com.lng.attendancecustomerservice.service.masters.DesignationService;
import com.lng.dto.masters.designation.DesignationDto;
import com.lng.dto.masters.designation.DesignationResponse;
import status.Status;
@Service
public class DesignationServiceImpl implements DesignationService{
	ModelMapper modelMapper=new ModelMapper();
	@Autowired
	DesignationRepository designationRepository;
	@Autowired
	CustomerRepository customerRepository;

	@Override
	public DesignationResponse saveDesignation(DesignationDto designationDto) {
		DesignationResponse response = new DesignationResponse();
		try{
			if(designationDto.getDesignationName() == null || designationDto.getDesignationName().isEmpty()) throw new Exception("Please enter Designation name");

			//if(CheckDesignationExists(designationDto.getDesignationName())) throw new Exception("Designation Name already exists");
			int a = designationRepository.findByRefCustIdAndDesignationName(designationDto.getRefCustId(), designationDto.getDesignationName());
			if(a == 0) {
				Customer customer = customerRepository.findCustomerByCustId(designationDto.getRefCustId());
				if(customer != null) {

					Designation designation = new Designation();
					designation.setCustomer(customer);
					designation.setDesignationName(designationDto.getDesignationName());
					designation.setDesigIsActive(true);
					designationRepository.save(designation);
					response.status = new Status(false,200, "Successfully created");
				}
				else{ 
					response.status = new Status(true,400, "Customer Not Found");
				}
			}
			else{ 
				response.status = new Status(true,400,"Designation Name already exist");
			}
		}catch(Exception e){
			response.status = new Status(true, 4000, e.getMessage());
		}

		return response;
	}

	/*
	 * private Boolean CheckDesignationExists(String designationName) { Designation
	 * designation = designationRepository.findByDesignationName(designationName);
	 * if(designation != null) { return true; } else { return false; } }
	 */
	@Override
	public DesignationResponse getAll() {
		DesignationResponse response = new DesignationResponse();
		try {
			List<Designation> designationList=designationRepository.findAllByDesigIsActive(true);
			response.setData1(designationList.stream().map(designation -> convertToDesignationDto(designation)).collect(Collectors.toList()));
			response.status = new Status(false,200, "successfully GetAll");
		}catch(Exception e) {
			response.status = new Status(true,3000, e.getMessage()); 

		}
		return response;
	}

	@Override
	public Status updateDesignationBydesignationId(DesignationDto designationDto) {
		Status status = null;
		try {
			if(designationDto.getDesignationName() == null || designationDto.getDesignationName().isEmpty()) throw new Exception("Please enter Designation name");
			if(designationDto.getDesignationId() == null || designationDto.getDesignationId() == 0) throw new Exception("Designation id is null or zero");
			if(designationDto.getRefCustId() == null || designationDto.getRefCustId() == 0) throw new Exception("RefCustomerId id is null or zero");
			Designation designation = designationRepository.findDesignationByDesignationId(designationDto.getDesignationId())	;	
			//if(CheckDesignationExists(designationDto.getDesignationName())) throw new Exception("Designation already exists");
			Customer customer = customerRepository.findCustomerByCustId(designationDto.getRefCustId());
			if(customer != null) {
				Designation de = designationRepository.findDesignationBydesignationNameAndCustomer_custId(designationDto.getDesignationName(), designationDto.getRefCustId());
				if(de == null) {
					designation.setCustomer(customer);
					designation.setDesignationName(designationDto.getDesignationName());
					designation.setDesigIsActive(true);
					designationRepository.save(designation);
					status = new Status(false,200, "Updated successfully");
				} else if (de.getDesignationId() == designationDto.getDesignationId()) { 

					designation.setCustomer(customer);
					designation.setDesignationName(designationDto.getDesignationName());
					designation.setDesigIsActive(true);
					designationRepository.save(designation);
					status = new Status(false,200, "Updated successfully");

				}
				else{ 
					status = new Status(true,400,"Designation Name already exist");
				}
			}

			else {
				status = new Status(false, 200, "Customer Not Found");

			}
		}
		catch(Exception e) {
			status = new Status(true, 4000, e.getMessage());
		}
		return status;
	}
	public DesignationDto convertToDesignationDto(Designation designation) {
		DesignationDto designationDto = modelMapper.map(designation,DesignationDto.class);
		designationDto.setRefCustId(designation.getCustomer().getCustId());
		designationDto.setCustName(designation.getCustomer().getCustName());

		return designationDto;
	}


	@Override
	public DesignationResponse deleteByDesignationId(Integer designationId) {
		DesignationResponse designationResponse=new DesignationResponse(); 
		try {
			Designation designation = designationRepository.findDesignationByDesignationId(designationId);
			int a = designationRepository.findEmployeeDesignationByDesignationDesignationId(designationId);
			if(designation!= null) {
			if(a == 0) {
			
					designationRepository.delete(designation);	
					designationResponse.status = new Status(false,200, "Successfully deleted");
				}else {
					designation.setDesigIsActive(false);
					designationRepository.save(designation);
					designationResponse.status = new Status(false,200, "The record has been just disabled as it has been used in another transaction");
				}

			} else {
				designationResponse.status = new Status(true,400, "Designation Not Found");
			}

		}catch(Exception e) { 
			designationResponse.status = new Status(true,500, e.getMessage());
		}

		return designationResponse;
	}

	@Override
	public DesignationResponse getDesignationByDesignationId(Integer designationId) {
		DesignationResponse response=new DesignationResponse();
		try {
			Designation designation = designationRepository.findDesignationByDesignationId(designationId);
			if(designation != null) {
				DesignationDto designationDto = convertToDesignationDto(designation);
				response.data = designationDto;
				response.status = new Status(false,200, "Success");
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
	public DesignationResponse getAllByCustId(Integer custId) {
		DesignationResponse response = new DesignationResponse();
		try {
			List<Designation> designationList=designationRepository.findAllByCustomer_CustIdAndDesigIsActive(custId, true);
			response.setData1(designationList.stream().map(designation -> convertToDesignationDto(designation)).collect(Collectors.toList()));
			
			if(response.getData1().isEmpty()) {
				response.status = new Status(true,400, "Designation Not Found"); 
				
			}else {
				response.status = new Status(false,200, "Success");
			}			
		}catch(Exception e) {
			response.status = new Status(true,500, "Something went wrong"); 

		}
		return response;
	}

}