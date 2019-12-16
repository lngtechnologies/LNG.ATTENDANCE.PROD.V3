package com.lng.attendancetabservice.service;

import com.lng.dto.tabService.EmployeeDto1;
import com.lng.dto.tabService.EmployeeResponse1;

import status.Status;

public interface EmployeeService {
	
	EmployeeResponse1  verifyEmpNameAndMobileNo(Integer refBrId,Integer refCustId,String empName,String empMobile);
	
	  Status  updateEmployee(EmployeeDto1 employeeDto1);
	  
	  

}
