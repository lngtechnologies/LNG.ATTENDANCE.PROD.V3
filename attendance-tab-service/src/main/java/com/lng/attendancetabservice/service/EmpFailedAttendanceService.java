package com.lng.attendancetabservice.service;

import java.util.List;

import com.lng.dto.empFailedAttendance.EmpFailedAttendanceDto;

import status.Status;

public interface EmpFailedAttendanceService {

	Status saveEmpFailedAttendance(List<EmpFailedAttendanceDto> empFailedAttendanceDtos);
}
