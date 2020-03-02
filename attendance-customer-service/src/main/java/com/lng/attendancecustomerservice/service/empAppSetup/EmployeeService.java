package com.lng.attendancecustomerservice.service.empAppSetup;

import java.util.Date;

import com.lng.dto.employeeAppSetup.AbsentDetailsResponse;
import com.lng.dto.employeeAppSetup.AppLeaveResponse;
import com.lng.dto.employeeAppSetup.EarlyLeaversResponse;
import com.lng.dto.employeeAppSetup.EmployeeDto;
import com.lng.dto.employeeAppSetup.LateComersResponse;
import com.lng.dto.employeeAppSetup.OtpResponseDto;
import com.lng.dto.employeeAppSetup.ResponseDto;

import status.StatusDto;

public interface EmployeeService {

	
	//Employee Application Setup Stage 1
	ResponseDto getByCustCodeAndEmpMobile(String custCode, String empMobile);
	
	OtpResponseDto generateOtp(Integer custId, Integer empId);
	
	StatusDto updateEmpAppStatus(EmployeeDto employeeDto);
	
	//EmpAppStatusResponseDto updateEmpAppStatus(Integer custId, Integer empId);
	
	//Employee Application Setup Stage 2
   // EmpAppStatusResponseDto updateEmpAppStatusStageTwo(EmployeeSetup2Dto employeeSetup2Dto);
	
	LateComersResponse  getLateComersDetails(Date dates,Integer custId,Integer empId);
	
	EarlyLeaversResponse  getEarlyLeaversDetails(Date dates,Integer custId,Integer empId);
	
	AbsentDetailsResponse getAbsentEmployeeDetails(Integer custId,Integer empId,Date dates);
	
	AppLeaveResponse      getAppLeaveDetails(Integer custId,Integer empId,Date dates);
	
	
	
	
}
