package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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

	private final Lock displayQueueLock = new ReentrantLock();
	
	@Override
	public DesignationResponse saveDesignation(DesignationDto designationDto) {
		DesignationResponse response = new DesignationResponse();
		final Lock displayLock = this.displayQueueLock;
		try{
			displayLock.lock();
			if(designationDto.getDesignationName() == null || designationDto.getDesignationName().isEmpty()) throw new Exception("Please enter Designation name");

			//if(CheckDesignationExists(designationDto.getDesignationName())) throw new Exception("Designation Name already exists");
			int a = designationRepository.findByRefCustIdAndDesignationName(designationDto.getRefCustId(), designationDto.getDesignationName());

			Designation designation1 = designationRepository.findByCustomer_CustIdAndDesignationNameAndDesigIsActive(designationDto.getRefCustId(), designationDto.getDesignationName(), false);

			if(a == 0) {
				Customer customer = customerRepository.findCustomerByCustId(designationDto.getRefCustId());
				if(customer != null) {

					Designation designation = new Designation();
					designation.setCustomer(customer);
					designation.setDesignationName(designationDto.getDesignationName());
					designation.setDesigIsActive(true);
					designationRepository.save(designation);
					response.status = new Status(false,200, "created");
					displayLock.unlock();
				}
				else{ 
					response.status = new Status(true,400, "Customer not found");
					displayLock.unlock();
				}
			} else if(designation1 != null){

				Customer customer = customerRepository.findCustomerByCustId(designationDto.getRefCustId());
				if(customer != null) {

					designation1.setCustomer(customer);
					designation1.setDesignationName(designationDto.getDesignationName());
					designation1.setDesigIsActive(true);
					designationRepository.save(designation1);
					response.status = new Status(false,200, "created");
					displayLock.unlock();
				}
				else{ 
					response.status = new Status(true,400, "Customer not found");
					displayLock.unlock();
				}
			}else {
				response.status = new Status(true,400,"Designation name already exists");
				displayLock.unlock();
			}
		}catch(Exception e){
			response.status = new Status(true, 500, "Oops..! Something went wrong..");
			displayLock.unlock();
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
			response.status = new Status(false,200, "Success");
		}catch(Exception e) {
			response.status = new Status(true, 500, "Oops..! Something went wrong.."); 

		}
		return response;
	}

	@Override
	public Status updateDesignationBydesignationId(DesignationDto designationDto) {
		final Lock displayLock = this.displayQueueLock;
		Status status = null;
		try {
			displayLock.lock();
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
					status = new Status(false,200, "updated");
					displayLock.unlock();
				} else if (de.getDesignationId() == designationDto.getDesignationId()) { 

					designation.setCustomer(customer);
					designation.setDesignationName(designationDto.getDesignationName());
					designation.setDesigIsActive(true);
					designationRepository.save(designation);
					status = new Status(false,200, "updated");
					displayLock.unlock();
				}
				else{ 

					status = new Status(true,400,"Designation name already exists");
					displayLock.unlock();
				}
			}

			else {
				status = new Status(false, 400, "Customer not found");
				displayLock.unlock();
			}
		}
		catch(Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");
			displayLock.unlock();
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
					designationResponse.status = new Status(false,200, "deleted");
				}else {
					designation.setDesigIsActive(false);
					designationRepository.save(designation);
					designationResponse.status = new Status(false,200, "The record has been disabled since it has been used in other transactions");
				}

			} else {
				designationResponse.status = new Status(true,400, "Designation not found");
			}

		}catch(Exception e) { 
			designationResponse.status = new Status(true, 500, "Oops..! Something went wrong..");
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
				response.status = new Status(true, 400, "Not found");
			}
		}catch(Exception e) {
			response.status = new Status(true, 500, "Oops..! Something went wrong.."); 

		}
		return response;
	}

	@Override
	public DesignationResponse getAllByCustId(Integer custId) {
		DesignationResponse response = new DesignationResponse();
		try {
			List<Designation> designationList=designationRepository.findAllByCustomer_CustId(custId);
			response.setData1(designationList.stream().map(designation -> convertToDesignationDto(designation)).collect(Collectors.toList()));

			if(response.getData1().isEmpty()) {
				response.status = new Status(false,400, "Not found"); 

			}else {
				response.status = new Status(false,200, "Success");
			}			
		}catch(Exception e) {
			response.status = new Status(true, 500, "Oops..! Something went wrong.."); 

		}
		return response;
	}

}