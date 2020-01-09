package com.lng.attendancetabservice.service;

import com.lng.dto.employee.OtpResponseDto;
import com.lng.dto.tabService.EmployeeDto1;
import com.lng.dto.tabService.EmployeeResponse1;
import com.lng.dto.tabService.EmployeeResponse2;

import status.Status;

public interface EmployeeService {
	
	EmployeeResponse1  verifyMobileNo(Integer refBrId,Integer refCustId,String empMobile);
	
	  Status  updateEmployee(EmployeeDto1 employeeDto1);
	  
	  OtpResponseDto generateOtp(String  empMobile,Integer refCustId);
	  
	  EmployeeResponse2  getShiftDetailsByEmpIdAndCustId(Integer empId,Integer custId);
	  
	  

}
