package com.lng.attendancecustomerservice.repositories.employeeAttendance;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.employeeAttendance.EmployeeAttendance;

@Repository
public interface EmployeeAttendanceRepository extends PagingAndSortingRepository<EmployeeAttendance, Integer> {

	EmployeeAttendance findByEmployee_EmpIdAndEmpAttendanceModeAndEmpAttendanceDatetimeAndEmpAttendanceLatitudeAndEmpAttendanceLongitude
							(Integer refEmpId, String empAttendanceMode, Date empAttendanceDatetime, Double empAttendanceLatitude, Double empAttendanceLongitude);
	
	
}
