package com.lng.attendancecustomerservice.repositories.masters;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.masters.Employee;
import com.lng.attendancecustomerservice.entity.masters.EmployeeLeave;

@Repository
public interface EmployeeLeaveRepository extends PagingAndSortingRepository<EmployeeLeave, Integer> {

	@Query(value = "call getEmpDetailsByBrIdForEmpLeave(?1)", nativeQuery = true)
	List<Object[]> findEmpDataByBrId(Integer brId);
	
	@Query(value = "call calNoDaysBetweenTwoDates(?1, ?2)", nativeQuery = true)
	int getNoOfDaysCount(Date fromDate, Date toDate );
}
