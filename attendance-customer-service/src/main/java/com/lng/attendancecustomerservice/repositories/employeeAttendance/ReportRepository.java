package com.lng.attendancecustomerservice.repositories.employeeAttendance;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.employeeAttendance.EmployeeAttendance;

@Repository
public interface ReportRepository extends PagingAndSortingRepository<EmployeeAttendance, Integer>{
	@Query(value = "CALL Sp_GetReportMasterData(?1)", nativeQuery = true)
	List<Object[]> GetReportMasterData(int custId);
	
	@Query(value = "CALL Sp_getPresentReport(?1)", nativeQuery = true)
	List<Object[]> GetPresentReport(String whereClause);
}
