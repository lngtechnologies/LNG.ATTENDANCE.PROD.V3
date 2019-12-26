package com.lng.attendancetabservice.serviceImpl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancetabservice.entity.Employee;
import com.lng.attendancetabservice.repositories.EmployeeRepository;
import com.lng.attendancetabservice.service.EmployeeService;
import com.lng.dto.tabService.EmployeeDto1;
import com.lng.dto.tabService.EmployeeDto2;
import com.lng.dto.tabService.EmployeeResponse1;

import status.Status;
@Service
public class EmployeeServiceImpl implements EmployeeService {
	@Autowired
	EmployeeRepository employeeRepository;

	@Override
	public EmployeeResponse1 verifyEmpNameAndMobileNo(Integer refBrId, Integer refCustId, String empName,
			String empMobile) {
		EmployeeResponse1  employeeResponse1  =  new  EmployeeResponse1();
		List<EmployeeDto2> EmployeeDtoList = new ArrayList<>();
		try {
			List<Object[]> employeeList =  employeeRepository.findEmployee(empName, empMobile);
			Employee employee  = employeeRepository.checkEmployeeExistsOrNot(refBrId, refCustId, empName, empMobile);
			if(employee == null) {
				employeeResponse1.status = new Status(false,400, "Invalid employee or mobile number");
			}else {
				for (Object[] p : employeeList) {	

					EmployeeDto2 employeeDto1 = new EmployeeDto2();
					employeeDto1.setEmpId(Integer.valueOf(p[0].toString()));
					employeeDto1.setEmpName(p[1].toString());
					EmployeeDtoList.add(employeeDto1);
					employeeResponse1.setData1(EmployeeDtoList);
					employeeResponse1.status = new Status(false,200, "success");
				}

			}

		}catch (Exception e){
			employeeResponse1.status = new Status(true, 500, "Oops..! Something went wrong..");


		}
		return employeeResponse1;
	}

	@Override
	public Status updateEmployee(EmployeeDto1 employeeDto1) {
		Status status = null;
		try {
			Employee employee = employeeRepository.findByempId(employeeDto1.getEmpId());
			if(employee != null){
				//Employee employee1  = new  Employee();
				employee.setEmpPicBlobPath(employeeDto1.getEmpPicBlobPath());
				employee.setEmpPresistedFaceId(employeeDto1.getEmpPresistedFaceId());
				employeeRepository.save(employee);
				status = new Status(false, 200, "successfully updated");

			}else {
				status = new Status(false,400,"Not found");
			}
		}catch(Exception e){
			status = new Status(true, 500,"Oops..! Something went wrong..");
		}
		return status;
	}

	// Convert base64 to byte
		public  byte[] base64ToByte(String base64) {
			byte[] decodedByte = Base64.getDecoder().decode(base64);
			return decodedByte;
		}

}
