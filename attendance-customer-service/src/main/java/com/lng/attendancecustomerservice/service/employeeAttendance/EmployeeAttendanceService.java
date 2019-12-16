package com.lng.attendancecustomerservice.service.employeeAttendance;

import java.util.List;

import com.lng.dto.employeeAttendance.CurrentDateDto;
import com.lng.dto.employeeAttendance.EmpSignOutDto;
import com.lng.dto.employeeAttendance.EmpSignOutResponse;
import com.lng.dto.employeeAttendance.EmployeeAttendanceDto;
import com.lng.dto.employeeAttendance.ShiftDetailsDto;
import com.lng.dto.employeeAttendance.ShiftResponseDto;

import status.Status;

public interface EmployeeAttendanceService {

	// Status save(List<EmployeeAttendanceDto> employeeAttendanceDtos);
	
	ShiftResponseDto getShiftDetailsByEmpId(Integer empId);
	
	EmpSignOutResponse getOfficeSignOutDetailsByEmpId(Integer empId);
	
	Status saveSignIn(List<EmployeeAttendanceDto> employeeAttendanceDtos);
	
	Status saveSignOut(List<EmployeeAttendanceDto> employeeAttendanceDtos);
	
	CurrentDateDto getCurrentDate();
}
