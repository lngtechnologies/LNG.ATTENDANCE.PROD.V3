package com.lng.attendancecustomerservice.service.masters;

import java.util.Date;

import com.lng.dto.masters.empLeaveApproveOrCancel.EmpLeaveResponseDto;
import com.lng.dto.masters.employeeLeave.EmployeeLeaveDto;

import status.Status;

public interface EmpLeaveApproveOrCancelService {

	EmpLeaveResponseDto getByLoginIdAndCustID(Integer loginId, Integer custId);
	
	Status empApproveLeave(EmployeeLeaveDto employeeLeaveDto);
	
	Status empRejectLeave(EmployeeLeaveDto employeeLeaveDto);
	
	EmpLeaveResponseDto getEmpLeaveAppByLoginIdAndCustID(Integer loginId, Integer custId);
	
	Status empApproveCancelLeave(EmployeeLeaveDto employeeLeaveDto);
	
	EmpLeaveResponseDto getByLoginIdAndCustIDAndEmpId(Integer loginId, Integer custId,Date empLeaveFrom,Date empLeaveTo);
}
