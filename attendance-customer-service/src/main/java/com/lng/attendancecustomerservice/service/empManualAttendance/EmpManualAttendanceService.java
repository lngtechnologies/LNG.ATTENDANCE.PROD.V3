package com.lng.attendancecustomerservice.service.empManualAttendance;

import java.util.Date;
import java.util.List;

import com.lng.dto.empAttendance.EmpAttendanceDto;
import com.lng.dto.empAttendance.EmpAttendanceParamDto2;
import com.lng.dto.empAttendance.EmpAttendanceResponse;

public interface EmpManualAttendanceService {

	EmpAttendanceResponse    getEmpAttendanceByDepartment_deptIdAndEmpAttendanceDatetime(Integer deptId ,Date empAttendanceDatetime );

	//EmpAttendanceResponse    saveEmpAttendanceByRefEmpIdAndShift_ShiftStartAndEmpAttendanceDatetime(Integer refEmpId ,String shiftStart,Date empAttendanceDatetime );
	EmpAttendanceResponse     saveEmpAttendance(List<EmpAttendanceDto> EmpAttendanceDtos);
    
	EmpAttendanceResponse     getEmpAttendanceDetailsByCustomer_custIdAndEmpAttendanceDatetimeAndEmployee_EmpName(Integer custId,Date empAttendanceDatetime,String empName );
	
	EmpAttendanceResponse searchEmployeeByNameAndRefCustIdAndEmpAttendanceDatetime(String emp,Integer  refCustId,Date empAttendanceDatetime);
	
	EmpAttendanceResponse  updateEmpOverRideAttendance(EmpAttendanceParamDto2 empAttendanceParamDto2);
}
