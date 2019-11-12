package com.lng.attendancecustomerservice.service.empAttendance;

import java.util.Date;
import java.util.List;

import com.lng.dto.empAttendance.EmpAttendanceDto;
import com.lng.dto.empAttendance.EmpAttendanceResponse;

public interface EmpAttendanceService {
	
      EmpAttendanceResponse    getEmpAttendanceByDepartment_deptIdAndEmpAttendanceDatetime(Integer deptId ,Date empAttendanceDatetime );
      
      //EmpAttendanceResponse    saveEmpAttendanceByRefEmpIdAndShift_ShiftStartAndEmpAttendanceDatetime(Integer refEmpId ,String shiftStart,Date empAttendanceDatetime );
      EmpAttendanceResponse     saveEmpAttendance(List<EmpAttendanceDto> EmpAttendanceDtos);
	}
