package com.lng.attendancetabservice.serviceImpl;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lng.attendancetabservice.entity.Employee;
import com.lng.attendancetabservice.entity.EmployeePic;
import com.lng.attendancetabservice.repositories.EmployeePicRepository;
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
	@Autowired
	EmployeePicRepository employeePicRepository;


	@Override
	public EmployeeResponse1 verifyEmpNameAndMobileNo(Integer refBrId, Integer refCustId, String empName,String empMobile) {
		EmployeeResponse1  employeeResponse1  =  new  EmployeeResponse1();
		try {

			List<Object[]> employeeList =  employeeRepository.findEmployee(empMobile);
			Employee employee  = employeeRepository.checkEmployeeExistsOrNot(refBrId, refCustId,empMobile);

			if(employee == null) {
				employeeResponse1.status = new Status(true,400, "Invalid mobile number");
			} else {
				for (Object[] p : employeeList) {	

					EmployeeDto2 employeeDto1 = new EmployeeDto2();
					employeeDto1.setEmpId(Integer.valueOf(p[0].toString()));
					employeeDto1.setEmpName(p[1].toString());
					employeeResponse1.setData1(employeeDto1);
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
		EmployeePic employeePic = new EmployeePic();
		try {
			Employee employee = employeeRepository.findByempId(employeeDto1.getEmpId());
			if(employee != null){
				employee.setEmpPresistedFaceId(employeeDto1.getEmpPresistedFaceId());
				employeeRepository.save(employee);
				
					employeePic = employeePicRepository.findByEmployee_EmpId(employee.getEmpId());

					if(employeePic == null) {
						EmployeePic employeePic1 = new EmployeePic();
						employeePic1.setEmployee(employee);
						employeePic1.setEmployeePic(base64ToByte(employeeDto1.getEmployeePic()));
						employeePicRepository.save(employeePic1);
						status = new Status(false, 200, "successfully updated");

					} else {
						employeePic.setEmployeePic(base64ToByte(employeeDto1.getEmployeePic()));
						employeePicRepository.save(employeePic);
						status = new Status(false, 200, "successfully updated");
					}
				
			}else {
				status = new Status(false,400," Employee Not found");
			}
		}catch(Exception e){
			status = new Status(true, 500, "Oops..! Something went wrong..");
		}
		return status;
	}

	// Convert base64 to byte
	public  byte[] base64ToByte(String base64) {
		byte[] decodedByte = Base64.getDecoder().decode(base64);
		return decodedByte;
	}
}

