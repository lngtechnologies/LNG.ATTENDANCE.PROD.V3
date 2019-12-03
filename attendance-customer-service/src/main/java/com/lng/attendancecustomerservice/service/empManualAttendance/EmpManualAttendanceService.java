package com.lng.attendancecustomerservice.service.empManualAttendance;

import java.util.Date;
import java.util.List;

import com.lng.dto.empAttendance.EmpAttendanceDto;
import com.lng.dto.empAttendance.EmpAttendanceParamDto2;
import com.lng.dto.empAttendance.EmpAttendanceResponse;
import com.lng.dto.employeeAttendance.EmployeeAttendanceDto;

import status.Status;

public interface EmpManualAttendanceService {

	EmpAttendanceResponse    getEmpAttendanceByDepartment_deptIdAndEmpAttendanceDatetime(Integer deptId ,Date empAttendanceDate );

	//EmpAttendanceResponse    saveEmpAttendanceByRefEmpIdAndShift_ShiftStartAndEmpAttendanceDatetime(Integer refEmpId ,String shiftStart,Date empAttendanceDatetime );
	/*EmpAttendanceResponse     saveEmpAttendance(List<EmpAttendanceDto> EmpAttendanceDtos);

	EmpAttendanceResponse     getEmpAttendanceDetailsByCustomer_custIdAndEmpAttendanceDatetimeAndEmployee_EmpName(Integer custId,Date empAttendanceDatetime,String empName );*/
	
	EmpAttendanceResponse searchEmployeeByNameAndRefCustIdAndEmpAttendanceDatetime(String emp,Integer  refCustId,Date empAttendanceDatetime);

	Status saveSignIn(List<EmployeeAttendanceDto> employeeAttendanceDtos);

	Status saveSignOut(List<EmployeeAttendanceDto> employeeAttendanceDtos);
	
	Status  updateEmpOverRideAttendance(EmployeeAttendanceDto employeeAttendanceDto);
}
