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
			int a  = designationRepository.findByRefCustIdAndDesignationName(designationDto.getRefCustId(), designationDto.getDesignationName());
			if(a == 0) {
				Customer customer = customerRepository.findCustomerByCustId(designationDto.getRefCustId());
				if(customer != null) {

					Designation designation = new Designation();
					designation.setCustomer(customer);
					designation.setDesignationName(designationDto.getDesignationName());
					designationRepository.save(designation);
					response.status = new Status(false,200, "successfully created");
				}
				else{ 
					response.status = new Status(true,400, "CustomerId Not Found");
				}
			}
			else{ 
				response.status = new Status(true,400,"DesignationName already exist for Customer :" + designationDto.getRefCustId());
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
			List<Designation> designationList=designationRepository.findAll();
			response.setData(designationList.stream().map(designation -> convertToDesignationDto(designation)).collect(Collectors.toList()));
			response.status = new Status(false,200, "successfully  GetAll");
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
			Designation 	designation = designationRepository.findDesignationByDesignationId(designationDto.getDesignationId())	;		
			//if(CheckDesignationExists(designationDto.getDesignationName())) throw new Exception("Designation already exists");
			Customer customer = customerRepository.findCustomerByCustId(designationDto.getRefCustId());
			if(customer != null) {
				Designation de = designationRepository.findDesignationBydesignationNameAndCustomer_custId(designationDto.getDesignationName(), designationDto.getRefCustId());
				if(de == null) {
					designation.setCustomer(customer);
					designation.setDesignationName(designationDto.getDesignationName());
					designationRepository.save(designation);
					status = new Status(false,200, "Updated successfully");
				} else if (de.getDesignationId() == designationDto.getDesignationId()) { 

					designation.setCustomer(customer);
					designation.setDesignationName(designationDto.getDesignationName());
					designationRepository.save(designation);
					status = new Status(false,200, "Updated successfully");

				}
				else{ 
					status = new Status(true,400,"DesignationName already exist for Customer :" + designationDto.getRefCustId());
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
	public DesignationDto convertToDesignationDto(Designation designation) {
		DesignationDto  designationDto = modelMapper.map(designation,DesignationDto.class);
		designationDto.setRefCustId(designation.getCustomer().getCustId());
		designationDto.setCustName(designation.getCustomer().getCustName());

		return designationDto;
	}


	@Override
	public DesignationResponse deleteByDesignationId(Integer designationId) {
		DesignationResponse designationResponse=new DesignationResponse(); 
		try {

			int a = designationRepository.findEmployeeDesignationByDesignationDesignationId(designationId);
			if(a == 0) {
				Designation designation = designationRepository.findDesignationByDesignationId(designationId);
				if(designation!= null) {
					designationRepository.delete(designation);					
					designationResponse.status = new Status(false,200, "successfully deleted");
				}

			} else  {
				designationResponse.status = new Status(true,400, "Cannot Delete");
			}

		}catch(Exception e) { 
			designationResponse.status = new Status(true,400, "DesignationId Not Found");
		}

		return designationResponse;
	}

}
