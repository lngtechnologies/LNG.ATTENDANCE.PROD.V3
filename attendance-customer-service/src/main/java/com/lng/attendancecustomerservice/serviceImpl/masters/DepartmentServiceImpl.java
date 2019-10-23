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
public class DepartmentServiceImpl  implements DepartmentService {

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
					departmentRepository.save(department);
					response.status = new Status(false,200, "successfully created");

				}
				else{ 
					response.status = new Status(true,400, "CustomerId Not Found");
				}
			}
			else{ 
				response.status = new Status(true,400,"DepartmentName already exist for branch :" + departmentDto.getRefCustId());
			}

		}catch(Exception ex){
			response.status = new Status(true, 4000, ex.getMessage());
		}

		return response;
	}

	/*
	 * private Boolean CheckDepartmentExists(String departmentName) { Department
	 * department = departmentRepository.findByDepartmentName(departmentName);
	 * if(department != null) { return true; } else { return false; } }
	 */
	@Override
	public DepartmentResponse getAll() {
		DepartmentResponse response = new DepartmentResponse();
		try {
			List<Department> stateList=departmentRepository.findAll();
			response.setData(stateList.stream().map(department -> convertToDepartmentDto(department)).collect(Collectors.toList()));
			response.status = new Status(false,200, "successfully  GetAll");
		}catch(Exception e) {
			response.status = new Status(true,3000, e.getMessage()); 

		}
		return response;
	}


	@Override
	public Status updateDepartmentByDepartmentId(DepartmentDto departmentDto) {
		Status status = null;
		//DepartmentResponse response = new DepartmentResponse();
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
					departmentRepository.save(department);
					status = new Status(false, 200, "Updated successfully");
				} else if (de.getDeptId() == departmentDto.getDeptId()) { 

					department = modelMapper.map(departmentDto,Department.class);
					department.setCustomer(customer);
					departmentRepository.save(department);
					status = new Status(false, 200, "Updated successfully");
				}
				else{ 
					status = new Status(true,400,"DepartmentName already exist for Customer :" + departmentDto.getRefCustId());
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

			int a = departmentRepository.findEmployeeDepartmentByDepartmentDeptId(deptId);
			if(a == 0) {
				Department department = departmentRepository.findDepartmentByDeptId(deptId);
				if(department!= null) {
					departmentRepository.delete(department);					
					departmentResponse.status = new Status(false,200, "successfully deleted");
				}

			} else  {
				departmentResponse.status = new Status(true,400, "Cannot Delete");
			}

		}catch(Exception e) { 
			departmentResponse.status = new Status(true,400, "DepartmentId Not Found");
		}

		return departmentResponse;
	}


	public DepartmentDto convertToDepartmentDto(Department department) {
		DepartmentDto departmentDto = modelMapper.map(department,DepartmentDto.class);
		departmentDto.setRefCustId(department.getCustomer().getCustId());
		departmentDto.setCustName(department.getCustomer().getCustName());
		return departmentDto;
	}

}
