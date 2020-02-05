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
	
	@Query(value = "CALL Sp_GetEmployeMasterData(?1, ?2)", nativeQuery = true)
	List<Object[]> GetEmployeMasterData(int custId, int empId);

	
	@Query(value = "CALL Sp_getPresentReport(?1)", nativeQuery = true)
	List<Object[]> GetPresentReport(String whereClause);
	
	@Query(value = "CALL Sp_getAbsentReport(?1, ?2)", nativeQuery = true)
	List<Object[]> GetAbsentReport(String whereClause, String date);
	
	@Query(value = "CALL Sp_getSummaryReportByEmp(?1, ?2, ?3)", nativeQuery = true)
	List<Object[]> GetEmployeeSummaryReport(String sDate, String eDate, int empId);
}
