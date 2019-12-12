package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.CustLeave;
import com.lng.attendancecustomerservice.entity.masters.Customer;
import com.lng.attendancecustomerservice.entity.masters.EmpLeave;
import com.lng.attendancecustomerservice.repositories.masters.CustLeaveRepository;
import com.lng.attendancecustomerservice.repositories.masters.CustomerRepository;
import com.lng.attendancecustomerservice.repositories.masters.EmpLeaveRepository;
import com.lng.attendancecustomerservice.service.masters.CustLeaveService;
import com.lng.dto.masters.custLeave.CustLeaveResponse;
import com.lng.dto.masters.custLeave.custLeaveDto;

import status.Status;

@Service
public class CustLeaveServiceImpl implements CustLeaveService {

	ModelMapper modelMapper=new ModelMapper();

	@Autowired
	CustLeaveRepository custLeaveRepository;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	EmpLeaveRepository empLeaveRepository;

	@Override
	public CustLeaveResponse saveCustLeave(custLeaveDto custLeaveDto) {
		CustLeaveResponse custLeaveResponse =  new  CustLeaveResponse();
		CustLeave custLeave  =  new CustLeave();
		try {
			if(custLeaveDto.getCustLeaveName() == null || custLeaveDto.getCustLeaveName().isEmpty()) throw new Exception("Please enter CustLeave name");
			int a =  custLeaveRepository.findByRefCustIdAndCustLeaveName(custLeaveDto.getRefCustId(), custLeaveDto.getCustLeaveName());
			
			
			if(a == 0) {
				Customer customer = customerRepository.findCustomerByCustId(custLeaveDto.getRefCustId());
				if(customer != null) {
					custLeave.setCustomer(customer);
					custLeave.setCustLeaveName(custLeaveDto.getCustLeaveName());
					custLeaveRepository.save(custLeave);
					custLeaveResponse.status = new Status(false,200, "successfully created");

				}
				else{ 
					custLeaveResponse.status = new Status(true,400, "Customer not found");
				}
			}
			else{ 
				custLeaveResponse.status = new Status(true,400,"CustLeave name already exist");
			}
		}catch(Exception e) {
			custLeaveResponse.status = new Status(true, 500, "Oops..! Something went wrong");
		}
		return custLeaveResponse;
	}

	@Override
	public CustLeaveResponse getAll() {
		CustLeaveResponse custLeaveResponse =  new  CustLeaveResponse();
		try {
			List<CustLeave> custLeaveList=custLeaveRepository.findAll();
			custLeaveResponse.setData1(custLeaveList.stream().map(custLeave -> convertTocustLeaveDto(custLeave)).collect(Collectors.toList()));

			if(custLeaveResponse.getData1().isEmpty()) {
				custLeaveResponse.status = new Status(false,400, "Not found"); 

			}else {
				custLeaveResponse.status = new Status(false,200, "Success");
			}			
		}catch(Exception e) {
			custLeaveResponse.status = new Status(true, 500, "Oops..! Something went wrong"); 

		}
		return custLeaveResponse;
	}

	@Override
	public Status updateCustLeaveByCustLeaveId(custLeaveDto custLeaveDto) {
		Status status = null;
		try {
			if(custLeaveDto.getCustLeaveName() == null || custLeaveDto.getCustLeaveName().isEmpty()) throw new Exception("Please enter CustLeave name");
			if(custLeaveDto.getCustLeaveId() == null || custLeaveDto.getCustLeaveId() == 0) throw new Exception("CustLeave id is null or zero");
			if(custLeaveDto.getRefCustId() == null || custLeaveDto.getRefCustId() == 0) throw new Exception("Customer id is null or zero");

			CustLeave custLeave =  custLeaveRepository.findCustLeaveByCustLeaveId(custLeaveDto.getCustLeaveId());
			Customer customer = customerRepository.findCustomerByCustId(custLeaveDto.getRefCustId());
			if(customer != null) {
				CustLeave cl = 	custLeaveRepository.findCustLeaveBycustLeaveNameAndCustomer_custId(custLeaveDto.getCustLeaveName(), custLeaveDto.getRefCustId());
				if(cl == null) {
					custLeave = modelMapper.map(custLeaveDto,CustLeave.class);
					custLeave.setCustomer(customer);
					custLeaveRepository.save(custLeave);
					status = new Status(false, 200, "successfully updated");
				} else if (cl.getCustLeaveId() == custLeaveDto.getCustLeaveId()) { 

					custLeave = modelMapper.map(custLeaveDto,CustLeave.class);
					custLeave.setCustomer(customer);
					custLeaveRepository.save(custLeave);
					status = new Status(false, 200, "successfully updated");
				}
				else{ 
					status = new Status(true,400,"CustLeave name already exist");
				}
			}

			else {
				status = new Status(false, 200, "Customer not found");

			}
		}
		catch(Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong");
		}
		return status;
	}

	@Override
	public CustLeaveResponse deleteByCustLeaveId(Integer custLeaveId) {
		CustLeaveResponse custLeaveResponse =  new  CustLeaveResponse();
		try {
			CustLeave custLeave =  custLeaveRepository.findCustLeaveByCustLeaveId(custLeaveId);
			if(custLeave != null) {
				List<EmpLeave>  empLeave = empLeaveRepository.findByCustLeave_CustLeaveId(custLeaveId);
				if(empLeave.isEmpty()) {
					custLeaveRepository.delete(custLeave);
					custLeaveResponse.status = new Status(false, 200, "successfully deleted");
				}else {
					custLeaveResponse.status = new Status(false, 200, "The record has been disabled since it has been used in other transactions");
				}
			}else {
				custLeaveResponse.status = new Status(true, 400, "CustLeave  not found");
			}

		}catch(Exception e) {
			custLeaveResponse.status = new Status(true, 500, "Oops..! Something went wrong");
		}
		return custLeaveResponse;
	}

	@Override
	public CustLeaveResponse getCustLeaveByCustLeaveId(Integer custLeaveId) {
		CustLeaveResponse custLeaveResponse =  new  CustLeaveResponse();

		try {
			CustLeave custLeave = custLeaveRepository.findCustLeaveByCustLeaveId(custLeaveId);
			if(custLeave != null) {
				custLeaveDto custLeaveDto = convertTocustLeaveDto(custLeave);
				custLeaveResponse.data = custLeaveDto;
				custLeaveResponse.status = new Status(false,200, "Success");
			}
			else {
				custLeaveResponse.status = new Status(true, 4000, "Not found");
			}
		}catch(Exception e) {
			custLeaveResponse.status = new Status(true, 500, "Oops..! Something went wrong"); 

		}
		return custLeaveResponse;
	}

	@Override
	public CustLeaveResponse getAllByCustId(Integer custId) {
		CustLeaveResponse custLeaveResponse =  new  CustLeaveResponse();

		try {
			List<CustLeave> custLeaveList=custLeaveRepository.findCustLeaveByCustomer_custId(custId);
			custLeaveResponse.setData1(custLeaveList.stream().map(custLeave -> convertTocustLeaveDto(custLeave)).collect(Collectors.toList()));

			if(custLeaveResponse.getData1().isEmpty()) {
				custLeaveResponse.status = new Status(false,400, "Not Found"); 

			}else {
				custLeaveResponse.status = new Status(false,200, "success");
			}			
		}catch(Exception e) {
			custLeaveResponse.status = new Status(true,500, "Oops..! Something went wrong"); 

		}
		return custLeaveResponse;
	}

	public custLeaveDto convertTocustLeaveDto(CustLeave custLeave) {
		custLeaveDto custLeaveDto1 = modelMapper.map(custLeave,custLeaveDto.class);
		custLeaveDto1.setRefCustId(custLeave.getCustomer().getCustId());
		custLeaveDto1.setCustName(custLeave.getCustomer().getCustName());
		return custLeaveDto1;
	}

}
