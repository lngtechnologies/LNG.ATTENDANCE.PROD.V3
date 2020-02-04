package com.lng.attendancetabservice.service;

import com.lng.dto.tabService.EmpAttendanceDto1;

import status.Status;

public interface EmpAttendanceService {

 Status saveEmpAttndIn(EmpAttendanceDto1 empAttendanceDto1);
 
 Status saveEmpAttndOut(EmpAttendanceDto1 empAttendanceDto1);
 
 //Status saveEmployeeAttnd(EmpAttendanceDto1 empAttendanceDto1);
	
}
