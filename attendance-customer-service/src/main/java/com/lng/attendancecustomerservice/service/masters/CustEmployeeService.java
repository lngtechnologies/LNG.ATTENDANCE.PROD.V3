package com.lng.attendancecustomerservice.service.masters;


import com.lng.dto.masters.custEmployee.CustEmployeeDto;
import com.lng.dto.masters.custEmployee.CustEmployeeListResponse;
import com.lng.dto.masters.custEmployee.CustEmployeeStatus;

import status.Status;

public interface CustEmployeeService {

	CustEmployeeStatus save(CustEmployeeDto custEmployeeDto);

	CustEmployeeListResponse findEmployeeByEmpId(Integer empId);

	CustEmployeeListResponse findEmployeeByCustId(Integer custId);

	CustEmployeeListResponse findAll();

	CustEmployeeStatus updateEmployee(CustEmployeeDto custEmployeeDto);

	CustEmployeeStatus deleteEmployeeByEmpIdId(Integer empId);

	CustEmployeeListResponse searchEmployeeByEmpName(String empName);

	CustEmployeeListResponse FindEmployeeByRefLoginId(Integer refLoginId);

	Status checkEmpMobileNumExistOrNot(String empMobile, Integer custId);

	//CustEmployeeStatus deleteEmployeeByEmpId(Integer empId);

}
