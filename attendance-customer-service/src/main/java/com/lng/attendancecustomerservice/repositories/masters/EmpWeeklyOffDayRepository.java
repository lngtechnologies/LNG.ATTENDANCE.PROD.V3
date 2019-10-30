package com.lng.attendancecustomerservice.repositories.masters;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecustomerservice.entity.masters.EmpWeeklyOffDay;

public interface EmpWeeklyOffDayRepository extends PagingAndSortingRepository<EmpWeeklyOffDay, Integer> {

	@Query(value = "SELECT * FROM ttempweeklyoffday WHERE refEmpId = ?1" ,nativeQuery = true)
	EmpWeeklyOffDay findEEmpWeeklyOffDayByEmployee_EmpId(Integer empId);
}
