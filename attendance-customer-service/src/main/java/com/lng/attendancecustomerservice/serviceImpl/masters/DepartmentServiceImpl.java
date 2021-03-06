package com.lng.attendancecustomerservice.serviceImpl.masters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
import com.lng.dto.masters.department.DepartmentParam;
import com.lng.dto.masters.department.DepartmentParamResponse;
import com.lng.dto.masters.department.DepartmentResponse;

import status.Status;
@Service
public class DepartmentServiceImpl implements DepartmentService {

	ModelMapper modelMapper=new ModelMapper();
	@Autowired
	DepartmentRepository departmentRepository;

	@Autowired
	CustomerRepository customerRepository;

	private final Lock displayQueueLock = new ReentrantLock();

	@Override
	public DepartmentResponse saveDepartment(DepartmentDto departmentDto) {
		DepartmentResponse response = new DepartmentResponse();
		final Lock displayLock = this.displayQueueLock;
		try{
			displayLock.lock();
			if(departmentDto.getDeptName() == null || departmentDto.getDeptName().isEmpty()) throw new Exception("Please enter Department name");

			int a = departmentRepository.findByRefCustIdAndDeptName(departmentDto.getRefCustId(), departmentDto.getDeptName());

			Department department1 = departmentRepository.findByCustomer_CustIdAndDeptNameAndDeptIsActive(departmentDto.getRefCustId(), departmentDto.getDeptName(), false);

			if(a == 0) {
				Customer customer = customerRepository.findCustomerByCustIdAndCustIsActive(departmentDto.getRefCustId(), true);
				if(customer != null) {

					Department department = new Department();
					department.setCustomer(customer);
					department.setDeptName(departmentDto.getDeptName());
					department.setDeptIsActive(true);
					departmentRepository.save(department);
					response.status = new Status(false,200, "created");

				}
				else{ 
					response.status = new Status(true,400, "Customer not found");

				}
			}else if(department1 != null){
				Customer customer = customerRepository.findCustomerByCustIdAndCustIsActive(departmentDto.getRefCustId(), true);
				if(customer != null) {

					department1.setCustomer(customer);
					department1.setDeptName(departmentDto.getDeptName());
					department1.setDeptIsActive(true);
					departmentRepository.save(department1);
					response.status = new Status(false,200, "created");

				}
				else{ 
					response.status = new Status(true,400, "Customer not found");

				}
			}else {

				response.status = new Status(true,400,"Department name already exists");

			}

		}catch(Exception ex){
			response.status = new Status(true, 500, "Oops..! Something went wrong..");

		}
		finally {
			displayLock.unlock();
		}
		return response;
	}


	@Override
	public DepartmentResponse getAll() {
		DepartmentResponse response = new DepartmentResponse();
		try {
			List<Department> stateList=departmentRepository.findAllByDeptIsActive(true);
			response.setData1(stateList.stream().map(department -> convertToDepartmentDto(department)).collect(Collectors.toList()));
			response.status = new Status(false,200, "Success");
		}catch(Exception e) {
			response.status = new Status(true, 500, "Oops..! Something went wrong.."); 

		}
		return response;
	}


	@Override
	public Status updateDepartmentByDepartmentId(DepartmentDto departmentDto) {
		Status status = null;
		final Lock displayLock = this.displayQueueLock;
		try {
			displayLock.lock();
			if(departmentDto.getDeptName() == null || departmentDto.getDeptName().isEmpty()) throw new Exception("Please enter Department name");
			if(departmentDto.getDeptId() == null || departmentDto.getDeptId() == 0) throw new Exception("Department id is null or zero");
			if(departmentDto.getRefCustId()	 == null || departmentDto.getRefCustId() == 0) throw new Exception("RefCustId id is null or zero");

			Department department = departmentRepository.findDepartmentByDeptIdAndDeptIsActive(departmentDto.getDeptId(), true);	
			Customer customer = customerRepository.findCustomerByCustIdAndCustIsActive(departmentDto.getRefCustId(), true);
			if(customer != null) {
				Department de = departmentRepository.findDepartmentBydeptNameAndCustomer_custId(departmentDto.getDeptName(), departmentDto.getRefCustId());
				if(de == null) {

					department = modelMapper.map(departmentDto,Department.class);
					department.setCustomer(customer);
					department.setDeptIsActive(true);
					departmentRepository.save(department);
					status = new Status(false, 200, "updated");

				} else if (de.getDeptId() == departmentDto.getDeptId()) { 

					department = modelMapper.map(departmentDto,Department.class);
					department.setCustomer(customer);
					department.setDeptIsActive(true);
					departmentRepository.save(department);
					status = new Status(false, 200, "updated");

				}
				else{ 

					status = new Status(true,400,"Department name already exists");

				}
			}

			else {
				status = new Status(false, 400, "Customer not found");

			}
		}
		catch(Exception e) {
			status = new Status(true, 500, "Oops..! Something went wrong..");

		}
		finally {
			displayLock.unlock();
		}
		return status;
	}


	@Override
	public DepartmentResponse deleteByDeptId(Integer deptId) {
		DepartmentResponse departmentResponse=new DepartmentResponse(); 
		try {
			Department department = departmentRepository.findDepartmentByDeptIdAndDeptIsActive(deptId, true);
			int a = departmentRepository.findEmployeeDepartmentByDepartmentDeptId(deptId);
			if(department!= null) {
				if(a == 0) {

					departmentRepository.delete(department);	
					departmentResponse.status = new Status(false,200, "deleted");

				} else {
					department.setDeptIsActive(false);
					departmentRepository.save(department);
					departmentResponse.status = new Status(false,200, "The record has been disabled since it has been used in other transactions");
				}
			}else {
				departmentResponse.status = new Status(true,400, "Department not found");
			}
		}catch(Exception e) { 
			departmentResponse.status = new Status(true, 500, "Oops..! Something went wrong..");
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
			Department department = departmentRepository.findDepartmentByDeptIdAndDeptIsActive(deptId, true);
			if(department != null) {
				DepartmentDto departmentDto = convertToDepartmentDto(department);
				response.data = departmentDto;
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
	public DepartmentResponse getAllByCustId(Integer custId) {
		DepartmentResponse response = new DepartmentResponse();
		try {
			List<Department> departments = departmentRepository.findAllByCustomer_CustId(custId);
			response.setData1(departments.stream().map(department -> convertToDepartmentDto(department)).collect(Collectors.toList()));
			if(response.getData1().isEmpty()) {
				response.status = new Status(false,400, "Not found");
			}else {
				response.status = new Status(false,200, "success");

			}
		}catch(Exception e) {
			response.status = new Status(true, 500, "Oops..! Something went wrong.."); 

		}
		return response;
	}


	@Override
	public DepartmentResponse getDepartmentDetailsByBrId(Integer brId) {
		DepartmentResponse response = new DepartmentResponse();
		try {
			List<Department> departments =departmentRepository.findDepartmentByBranch_brId(brId);
			response.setData1(departments.stream().map(department -> convertToDepartmentDto(department)).collect(Collectors.toList()));
			if(response.getData1().isEmpty()) {
				response.status = new Status(false,400, "Not found");
			}else {
				response.status = new Status(false,200, "success");

			}
		}catch(Exception e) {
			response.status = new Status(true, 500, "Oops..! Something went wrong.."); 

		}
		return response;
	}


	@Override
	public DepartmentParamResponse getDepartmentdetailByCustIdAndEmpId(Integer custId, Integer empId) {
		DepartmentParamResponse response = new DepartmentParamResponse();
		List<DepartmentParam> departmentList = new ArrayList<DepartmentParam>();
		try {
			List<Object[]> departments = departmentRepository.findDepartmentByCustomer_CustIdAndEmployee_EmpId(custId, empId);
			if(empId != 0) {
				if(!departments.isEmpty()) {
					for(Object[] e : departments ) {
						DepartmentParam departmentParam	 = new DepartmentParam();
						departmentParam.setDeptId(Integer.valueOf(e[0].toString()));
						departmentParam.setDeptName(e[1].toString());
						departmentList.add(departmentParam);
						response.setDepartmentList(departmentList);
						response.status = new Status(false, 200, "Success");
					}
				}else {
					response.status = new Status(false, 400, "No records found");
				}
			}else {
				List<Object[]>	departments2 =departmentRepository.getDepartmentDetailsForAdminByCustomer_CustIdAndEmployee_EmpId(custId, empId);
				if(!departments2.isEmpty()) {
					for(Object[] a : departments2 ) {
						DepartmentParam departmentParam1 = new DepartmentParam();
						departmentParam1.setDeptId(Integer.valueOf(a[0].toString()));
						departmentParam1.setDeptName(a[1].toString());
						departmentList.add(departmentParam1);
						response.setDepartmentList(departmentList);
						response.status = new Status(false, 200, "Success");
					}
				}else {
					response.status = new Status(false, 400, "No records found");
				}
			}

		}catch(Exception e) {
			response.status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return response;
	}
}