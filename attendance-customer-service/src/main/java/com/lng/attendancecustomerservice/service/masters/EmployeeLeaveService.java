package com.lng.attendancecustomerservice.service.masters;

import com.lng.dto.masters.employeeLeave.BranchListDto;
import com.lng.dto.masters.employeeLeave.CustLeaveTrypeListDto;
import com.lng.dto.masters.employeeLeave.EmployeeDtatListDto;
import com.lng.dto.masters.employeeLeave.EmployeeLeaveDto;

import status.Status;

public interface EmployeeLeaveService {

	BranchListDto getBranchListByCustId(Integer custId);
	
	CustLeaveTrypeListDto getLeaveListByCustId(Integer custId);
	
	EmployeeDtatListDto getEmpDataByBrID(Integer brId);
	
	Status saveEmpLeave(EmployeeLeaveDto employeeLeaveDto);
}
