package com.lng.attendancecustomerservice.repositories.masters;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecustomerservice.entity.masters.EmpWeeklyOffDay;

public interface EmpWeeklyOffDayRepository extends PagingAndSortingRepository<EmpWeeklyOffDay, Integer> {

	@Query(value = "SELECT * FROM ttempweeklyoffday WHERE refEmpId = ?1" ,nativeQuery = true)
	EmpWeeklyOffDay findEEmpWeeklyOffDayByEmployee_EmpId(Integer empId);
	
	@Query(value = "SELECT MAX(ewofd.fromDate) AS fromDate, ewofd.empWeeklyOffDayId, ewofd.refEmpId, ewofd.yearMonth, ewofd.dayOfWeek, ewofd.fromDate, ewofd.toDate FROM ttempweeklyoffday ewofd WHERE ewofd.refEmpId = ?1 AND ewofd.toDate IS NULL" ,nativeQuery = true)
	EmpWeeklyOffDay findEEmpWeeklyOffDayByEmpId(Integer empId);
	
	EmpWeeklyOffDay findEEmpWeeklyOffDayByEmployee_EmpIdAndDayOfWeekAndFromDate(Integer empId, String dayOfWeek, Date fromDate);
}
