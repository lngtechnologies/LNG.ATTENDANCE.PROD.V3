package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancecustomerservice.entity.masters.Customer;
import com.lng.attendancecustomerservice.entity.masters.Department;
import com.lng.attendancecustomerservice.repositories.masters.CustomerRepository;
import com.lng.attendancecustomerservice.repositories.masters.DepartmentRepository;
import com.lng.attendancecustomerservice.service.masters.DepartmentService;
import com.lng.dto.masters.department.DepartmentDto;
import com.lng.dto.masters.department.DepartmentResponse;

import status.Status;
@Service
public class DepartmentServiceImpl implements DepartmentService {

	ModelMapper modelMapper=new ModelMapper();
	@Autowired
	DepartmentRepository departmentRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Override
	public DepartmentResponse saveDepartment(DepartmentDto departmentDto) {
		DepartmentResponse response = new DepartmentResponse();
		try{
			if(departmentDto.getDeptName() == null || departmentDto.getDeptName().isEmpty()) throw new Exception("Please enter Department name");

			int a = departmentRepository.findByRefCustIdAndDeptName(departmentDto.getRefCustId(), departmentDto.getDeptName());
			if(a == 0) {
				Customer customer = customerRepository.findCustomerByCustId(departmentDto.getRefCustId());
				if(customer != null) {

					Department department = new Department();
					department.setCustomer(customer);
					department.setDeptName(departmentDto.getDeptName());
					department.setDeptIsActive(true);
					departmentRepository.save(department);
					response.status = new Status(false,200, "successfully created");

				}
				else{ 
					response.status = new Status(true,400, "CustomerId Not Found");
				}
			}
			else{ 
				response.status = new Status(true,400,"DepartmentName already exist for Customer");
			}

		}catch(Exception ex){
			response.status = new Status(true, 4000, ex.getMessage());
		}

		return response;
	}
	@Override
	public DepartmentResponse getAll() {
		DepartmentResponse response = new DepartmentResponse();
		try {
			List<Department> stateList=departmentRepository.findAllByDeptIsActive(true);
			response.setData1(stateList.stream().map(department -> convertToDepartmentDto(department)).collect(Collectors.toList()));
			response.status = new Status(false,200, "successfully GetAll");
		}catch(Exception e) {
			response.status = new Status(true,3000, e.getMessage()); 

		}
		return response;
	}


	@Override
	public Status updateDepartmentByDepartmentId(DepartmentDto departmentDto) {
		Status status = null;
		try {
			if(departmentDto.getDeptName() == null || departmentDto.getDeptName().isEmpty()) throw new Exception("Please enter Department name");
			if(departmentDto.getDeptId() == null || departmentDto.getDeptId() == 0) throw new Exception("Department id is null or zero");
			if(departmentDto.getRefCustId() == null || departmentDto.getRefCustId() == 0) throw new Exception("RefCustId id is null or zero");

			Department department = departmentRepository.findDepartmentByDeptId(departmentDto.getDeptId());	
			Customer customer = customerRepository.findCustomerByCustId(departmentDto.getRefCustId());
			if(customer != null) {
				Department de = departmentRepository.findDepartmentBydeptNameAndCustomer_custId(departmentDto.getDeptName(), departmentDto.getRefCustId());
				if(de == null) {

					department = modelMapper.map(departmentDto,Department.class);
					department.setCustomer(customer);
					department.setDeptIsActive(true);
					departmentRepository.save(department);
					status = new Status(false, 200, "Updated successfully");
				} else if (de.getDeptId() == departmentDto.getDeptId()) { 

					department = modelMapper.map(departmentDto,Department.class);
					department.setCustomer(customer);
					department.setDeptIsActive(true);
					departmentRepository.save(department);
					status = new Status(false, 200, "Updated successfully");
				}
				else{ 
					status = new Status(true,400,"DepartmentName already exist for Customer");
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


	@Override
	public DepartmentResponse deleteByDeptId(Integer deptId) {
		DepartmentResponse departmentResponse=new DepartmentResponse(); 
		try {
			Department department = departmentRepository.findDepartmentByDeptId(deptId);
			int a = departmentRepository.findEmployeeDepartmentByDepartmentDeptId(deptId);
			if(department!= null) {
				if(a == 0) {

					departmentRepository.delete(department);	
					departmentResponse.status = new Status(false,200, "successfully deleted");

				} else {
					department.setDeptIsActive(false);
					departmentRepository.save(department);
					departmentResponse.status = new Status(false,200, "The record has been just disabled as it is already used");
				}
			}else {
				departmentResponse.status = new Status(true,400, "DepartmentId Not Found");
			}
		}catch(Exception e) { 
			departmentResponse.status = new Status(true,400, e.getMessage());
		}

		return departmentResponse;
	}


	public DepartmentDto convertToDepartmentDto(Department department) {
		DepartmentDto departmentDto = modelMapper.map(department,DepartmentDto.class);
		departmentDto.setRefCustId(department.getCustomer().getCustId());
		departmentDto.setCustName(department.getCustomer().getCustName());
		return departmentDto;
	}

	@Override
	public DepartmentResponse getDepartmentByDeptId(Integer deptId) {
		DepartmentResponse response=new DepartmentResponse();
		try {
			Department department = departmentRepository.findDepartmentByDeptId(deptId);
			if(department != null) {
				DepartmentDto departmentDto = convertToDepartmentDto(department);
				response.data = departmentDto;
				response.status = new Status(false,200, "successfully GetDepartmentDetails");
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
	public DepartmentResponse getAllByCustId(Integer custId) {
		DepartmentResponse response = new DepartmentResponse();
		try {
			List<Department> departments = departmentRepository.findAllByCustomer_CustIdAndDeptIsActive(custId, true);
			response.setData1(departments.stream().map(department -> convertToDepartmentDto(department)).collect(Collectors.toList()));
			if(response.getData1().isEmpty()) {
				response.status = new Status(true,400, "Department Not Found");
			}else {
				response.status = new Status(false,200, "success");

			}
		}catch(Exception e) {
			response.status = new Status(true,500, "Something went wrong"); 

		}
		return response;
	}
}