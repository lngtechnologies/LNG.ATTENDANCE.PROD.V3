package com.lng.attendancecustomerservice.service.masters;


import com.lng.dto.masters.custEmployee.CustEmployeeDto;
import com.lng.dto.masters.custEmployee.CustEmployeeListResponse;
import com.lng.dto.masters.custEmployee.CustEmployeeResponse;
import com.lng.dto.masters.custEmployee.CustEmployeeStatus;

public interface CustEmployeeService {

	CustEmployeeStatus save(CustEmployeeDto custEmployeeDto);
	
	CustEmployeeResponse findEmployeeByEmpId(Integer empId);
	
	CustEmployeeListResponse findAll();
	
	CustEmployeeStatus updateEmployee(CustEmployeeDto custEmployeeDto);
	
	CustEmployeeStatus deleteEmployeeByEmpIdId(Integer empId);
	
	CustEmployeeListResponse searchEmployeeByEmpName(String empName);
}
