package com.lng.attendancecustomerservice.service.employeeAttendance;

import java.util.List;

import com.lng.dto.employeeAttendance.EmployeeAttendanceDto;

import status.Status;

public interface EmployeeAttendanceService {

	Status save(List<EmployeeAttendanceDto> employeeAttendanceDtos);
}
