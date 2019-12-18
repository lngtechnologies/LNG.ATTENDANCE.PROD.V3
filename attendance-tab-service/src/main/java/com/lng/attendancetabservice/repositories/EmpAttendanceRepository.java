package com.lng.attendancetabservice.repositories;

import java.util.Date;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancetabservice.entity.EmpAttendance;

@Repository
public interface EmpAttendanceRepository extends PagingAndSortingRepository<EmpAttendance, Integer> {
						
	EmpAttendance findByEmployee_EmpIdAndEmpAttendanceInDatetime(Integer refEmpId, Date empAttendanceInDatetime);
	
	EmpAttendance findByEmpAttendanceId(Integer empAttendanceId);
	
	EmpAttendance findByEmployee_EmpIdAndEmpAttendanceDate(Integer refEmpId, Date empAttendanceDate);
	
	
	EmpAttendance findByEmployee_EmpIdAndEmpAttendanceOutDatetime(Integer refEmpId, Date empAttendanceOutDatetime);
	
	EmpAttendance findByEmployee_EmpIdAndEmpAttendanceDateAndEmpAttendanceOutModeAndEmpAttendanceOutDatetimeAndEmpAttendanceOutLatLong(Integer refEmpId, Date empAttendanceDate, String empAttendanceOutMode, Date empAttendanceOutDatetime,String empAttendanceOutLatLong);

	EmpAttendance findByEmployee_EmpIdAndEmpAttendanceInModeAndEmpAttendanceInDatetimeAndEmpAttendanceInLatLong(Integer refEmpId, String empAttendanceInMode, Date empAttendanceInDatetime,String empAttendanceInLatLong);
	
}
