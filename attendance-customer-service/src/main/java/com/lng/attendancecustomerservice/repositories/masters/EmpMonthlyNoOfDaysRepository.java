package com.lng.attendancecustomerservice.repositories.masters;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.lng.attendancecustomerservice.entity.masters.EmpMonthlyNoOfDays;

public interface EmpMonthlyNoOfDaysRepository extends PagingAndSortingRepository<EmpMonthlyNoOfDays, Integer>{

	@Query(value = "call WORKDAYS(?1)", nativeQuery = true)
	Integer findNoOfDaysByYearMonth(Date yearMonth);
	
	EmpMonthlyNoOfDays findByEmployee_EmpId(Integer empId);
}
