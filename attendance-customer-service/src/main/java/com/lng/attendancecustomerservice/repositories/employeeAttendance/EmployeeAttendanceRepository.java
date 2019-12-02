package com.lng.attendancecustomerservice.repositories.employeeAttendance;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.employeeAttendance.EmployeeAttendance;

@Repository
public interface EmployeeAttendanceRepository extends PagingAndSortingRepository<EmployeeAttendance, Integer> {

	EmployeeAttendance findByEmployee_EmpIdAndEmpAttendanceModeAndEmpAttendanceDatetimeAndEmpAttendanceLatitudeAndEmpAttendanceLongitude
							(Integer refEmpId, String empAttendanceMode, Date empAttendanceDatetime, Double empAttendanceLatitude, Double empAttendanceLongitude);
	
	EmployeeAttendance findByEmployee_EmpIdAndEmpAttendanceDate(Integer refEmpId, Date empAttendanceDate);
	
	EmployeeAttendance findByEmployee_EmpIdAndEmpAttendanceDateAndEmpAttendanceOutModeAndEmpAttendanceOutDatetimeAndEmpAttendanceOutLatLong(Integer refEmpId, Date empAttendanceDate, String empAttendanceOutMode, Date empAttendanceOutDatetime,String empAttendanceOutLatLong);

}
