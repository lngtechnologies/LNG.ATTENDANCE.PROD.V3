package com.lng.attendancecustomerservice.service.empAppSetup;

import com.lng.dto.masters.employeeLeave.CustLeaveTrypeListDto;
import com.lng.dto.masters.employeeLeave.EmpLeaveResponseDto;
import com.lng.dto.masters.employeeLeave.EmployeeLeaveDto;

import status.Status;

public interface EmpLeaveService {

	CustLeaveTrypeListDto getLeaveListByCustId(Integer custId);
	
	Status saveEmpLeave(EmployeeLeaveDto employeeLeaveDto);
	
	EmpLeaveResponseDto getEmpLeaveByEmpId(Integer empId);
	
}
