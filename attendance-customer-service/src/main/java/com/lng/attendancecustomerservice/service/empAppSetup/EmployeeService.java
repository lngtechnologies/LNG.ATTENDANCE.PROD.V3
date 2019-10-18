package com.lng.attendancecustomerservice.service.empAppSetup;

import com.lng.dto.employee.EmployeeDto;
import com.lng.dto.employee.OtpResponseDto;
import com.lng.dto.employee.ResponseDto;

import status.StatusDto;

public interface EmployeeService {

	
	//Employee Application Setup Stage 1
	ResponseDto getByCustCodeAndEmpMobile(String custCode, String empMobile);
	
	OtpResponseDto generateOtp(Integer custId, Integer empId);
	
	StatusDto updateEmpAppStatus(EmployeeDto employeeDto);
	
	//EmpAppStatusResponseDto updateEmpAppStatus(Integer custId, Integer empId);
	
	//Employee Application Setup Stage 2
   // EmpAppStatusResponseDto updateEmpAppStatusStageTwo(EmployeeSetup2Dto employeeSetup2Dto);
	
}
